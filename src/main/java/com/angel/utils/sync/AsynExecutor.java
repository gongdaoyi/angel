package com.angel.utils.sync;

import com.google.common.util.concurrent.*;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 异步任务执行器
 *
 * <p>基于Guava ListeningExecutorService实现的异步任务执行器，支持并发任务提交和结果收集</p>
 * <p>
 * 功能特点：
 * <ul>
 *   <li>支持多线程并发执行任务</li>
 *   <li>自动收集执行结果和失败信息</li>
 *   <li>支持任务状态查询（运行中、成功、失败）</li>
 *   <li>支持失败时不再提交新任务</li>
 *   <li>线程池自动管理，支持自定义大小</li>
 * </ul>
 * <p>
 * 使用示例：
 * <pre>
 * AsynExecutor executor = new AsynExecutor();
 * AsynExecutor.init(10); // 初始化10个线程
 *
 * // 提交任务
 * int taskId = executor.submitIndex(() -> {
 *     // 业务逻辑
 *     return "result";
 * });
 *
 * // 获取结果
 * AsynExecutorResult result = executor.getResultByIndex(taskId);
 * String value = result.getWithCallBack();
 * </pre>
 *
 * @author Angel Development Team
 * @version 1.0.0
 * @since 2025-01-01
 */
@Log4j2
public class AsynExecutor {

    /**
     * 默认线程池大小
     */
    private static final int DEFAULT_POOL_SIZE = 50;

    /**
     * 线程池空闲时间（秒）
     */
    private static final long KEEP_ALIVE_TIME = 60L;

    /**
     * 线程池单例锁对象
     */
    private static final Object LOCK_OBJ = new Object();

    /**
     * 全局线程池服务
     */
    private static ListeningExecutorService service;

    /**
     * 线程池是否已启动标志
     */
    private static Boolean isStarted = false;

    /**
     * 任务结果映射表（任务索引 -> 执行结果）
     */
    private final ConcurrentHashMap<Integer, AsynExecutorResult> resultMap = new ConcurrentHashMap<>();

    /**
     * 失败任务结果映射表（任务索引 -> 失败结果）
     */
    private final ConcurrentHashMap<Integer, AsynExecutorResult> failResultMap = new ConcurrentHashMap<>();

    /**
     * 任务索引计数器（线程安全）
     */
    private AtomicInteger indexNum = new AtomicInteger(0);

    /**
     * 运行中任务计数器（线程安全）
     */
    private AtomicInteger statusNum = new AtomicInteger(0);

    /**
     * 失败任务计数器（线程安全）
     */
    private AtomicInteger resultNum = new AtomicInteger(0);

    /**
     * 初始化线程池
     *
     * <p>使用单例模式确保只初始化一次线程池</p>
     *
     * @param size 线程池大小，小于等于0时使用默认值50
     */
    public static void init(int size) {
        if (!isStarted) {
            synchronized (LOCK_OBJ) {
                if (!isStarted) {
                    if (size <= 0) {
                        log.info("线程池大小无效，使用默认值: {}", DEFAULT_POOL_SIZE);
                        size = DEFAULT_POOL_SIZE;
                    }

                    service = MoreExecutors.listeningDecorator(
                            new ThreadPoolExecutor(
                                    size,
                                    size,
                                    KEEP_ALIVE_TIME,
                                    TimeUnit.SECONDS,
                                    new SynchronousQueue<>(),
                                    new CallerRunsPolicy()
                            )
                    );

                    isStarted = true;
                    log.info("异步执行器初始化完成，线程池大小: {}", size);
                }
            }
        }
    }

    /**
     * 提交任务（如果已有失败任务则不再提交）
     *
     * @param <T>  返回值类型
     * @param task 自定义任务对象
     * @return 任务索引，如果已有失败任务则返回-1
     */
    public <T> int submitIndexIfNoFail(CustCallable<T> task) {
        return submit(task, true);
    }

    /**
     * 提交任务（忽略失败状态）
     *
     * @param <T>  返回值类型
     * @param task 自定义任务对象
     * @return 任务索引
     */
    public <T> int submitIndex(CustCallable<T> task) {
        return submit(task, false);
    }

    /**
     * 内部提交任务方法
     *
     * @param <T>               返回值类型
     * @param task              自定义任务对象
     * @param isSubmitExistFail 是否在存在失败任务时阻止提交
     * @return 任务索引，如果阻止提交则返回-1
     */
    private <T> int submit(CustCallable<T> task, boolean isSubmitExistFail) {
        // 检查是否需要阻止提交
        if (isSubmitExistFail) {
            if (resultNum.get() != 0) {
                log.warn("存在失败任务，阻止新任务提交");
                return -1;
            }
        }

        // 提交任务到线程池
        ListenableFuture<T> custTask = service.submit(task::call);
        int index = indexNum.getAndIncrement();
        AsynExecutorResult<T> exeResult = new AsynExecutorResult<>(custTask);

        // 保存任务结果
        resultMap.put(index, exeResult);
        exeResult.setStatus(ExecutorStatus.RUNNING);
        statusNum.incrementAndGet();

        // 添加回调处理
        Futures.addCallback(custTask, new FutureCallback<T>() {
            /**
             * 任务成功回调
             */
            @Override
            public void onSuccess(T result) {
                log.debug("任务执行成功，索引: {}", index);
                exeResult.setResult(result);
                exeResult.setStatus(ExecutorStatus.SUCESS);
                exeResult.getCountDownLatch().countDown();
            }

            /**
             * 任务失败回调
             */
            @Override
            public void onFailure(Throwable t) {
                log.error("任务执行失败，索引: {}", index, t);
                exeResult.setException(t);
                exeResult.setStatus(ExecutorStatus.FAIL);
                failResultMap.put(index, exeResult);
                resultNum.incrementAndGet();
                exeResult.getCountDownLatch().countDown();
            }
        });

        return index;
    }

    /**
     * 获取所有任务结果列表
     *
     * @return 所有任务结果的ArrayList
     */
    public ArrayList<AsynExecutorResult> getAllResultList() {
        return new ArrayList<>(resultMap.values());
    }

    /**
     * 获取所有任务结果映射表
     *
     * @return 任务索引到结果的映射表
     */
    public ConcurrentHashMap<Integer, AsynExecutorResult> getAllResultMap() {
        return resultMap;
    }

    /**
     * 获取所有失败任务结果映射表
     *
     * @return 失败任务索引到结果的映射表
     */
    public ConcurrentHashMap<Integer, AsynExecutorResult> getAllFailResultMap() {
        return failResultMap;
    }

    /**
     * 根据索引获取任务结果
     *
     * @param index 任务索引
     * @return 任务执行结果，如果不存在则返回null
     */
    public AsynExecutorResult getResultByIndex(int index) {
        return resultMap.get(index);
    }

    /**
     * 获取当前执行状态（不阻塞）
     *
     * <p>如果有任务未完成，则返回该任务的状态</p>
     * <p>如果所有任务都成功，则返回成功状态</p>
     *
     * @return 当前执行状态
     */
    public ExecutorStatus getExecutorCurrentStatus() {
        Collection<AsynExecutorResult> resultColl = resultMap.values();
        for (AsynExecutorResult result : resultColl) {
            if (result.getStatus() != ExecutorStatus.SUCESS) {
                return result.getStatus();
            }
        }
        return ExecutorStatus.SUCESS;
    }

    /**
     * 获取最终执行状态（阻塞等待）
     *
     * <p>同步等待所有任务完成，返回最终状态</p>
     *
     * @return 最终执行状态
     */
    public ExecutorStatus getExecutorFinalStatus() {
        // 检查是否有失败任务
        if (resultNum.get() != 0) {
            return ExecutorStatus.FAIL;
        }

        // 等待所有任务完成
        Collection<AsynExecutorResult> resultColl = resultMap.values();
        for (AsynExecutorResult result : resultColl) {
            if (result.getStatus() == ExecutorStatus.FAIL) {
                return ExecutorStatus.FAIL;
            } else if (result.getStatus() == ExecutorStatus.RUNNING) {
                try {
                    result.getWithCallBack();
                } catch (Exception e) {
                    log.debug("等待任务完成时发生异常", e);
                    return ExecutorStatus.FAIL;
                }
            }
        }
        return ExecutorStatus.SUCESS;
    }

    /**
     * 重置执行器状态
     *
     * <p>清空所有任务结果和计数器，准备执行新的一批任务</p>
     */
    public void resetResultList() {
        log.info("重置异步执行器状态");
        resultMap.clear();
        failResultMap.clear();
        indexNum = new AtomicInteger(0);
        statusNum = new AtomicInteger(0);
        resultNum = new AtomicInteger(0);
    }
}
