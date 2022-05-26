package com.angel.sync;

import com.google.common.util.concurrent.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class AsynExecutor {
    private static final Logger logger = LoggerFactory.getLogger(AsynExecutor.class);
    private static final Object lockObj = new Object();
    private static ListeningExecutorService service;
    private static Boolean isStarted = false;
    private final ConcurrentHashMap<Integer, AsynExecutorResult> resultMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, AsynExecutorResult> failResultMap = new ConcurrentHashMap<>();
    private AtomicInteger indexNum = new AtomicInteger(0);
    private AtomicInteger statusNum = new AtomicInteger(0);
    private AtomicInteger resultNum = new AtomicInteger(0);

    public static void init(int size) {
        if (isStarted) {
        } else {
            synchronized (lockObj) {
                if (isStarted) {
                } else {
                    if (size <= 0) {
                        size = 50;
                    }
                    service = MoreExecutors.listeningDecorator(new ThreadPoolExecutor(size, size, 60L, TimeUnit.SECONDS,
                            new SynchronousQueue<Runnable>(), new CallerRunsPolicy()));
                    isStarted = true;
                }
            }
        }
    }

    public <T> int submitIndexIfNoFail(CustCallable<T> task) {
        return submit(task, true);
    }

    public <T> int submitIndex(CustCallable<T> task) {
        return submit(task, false);
    }

    private <T> int submit(CustCallable<T> task, boolean isSubmitExistFail) {
        if (isSubmitExistFail) {
            if (resultNum.get() != 0) {
                return -1;
            }
        }
        ListenableFuture<T> custTask = service.submit(task::call);
        int index = indexNum.getAndIncrement();
        AsynExecutorResult<T> exeResult = new AsynExecutorResult<T>(custTask);
        resultMap.put(index, exeResult);
        exeResult.setStatus(ExecutorStatus.RUNNING);
        statusNum.incrementAndGet();
        Futures.addCallback(custTask, new FutureCallback<T>() {
            @Override
            public void onSuccess(T result) {
                exeResult.setResult(result);
                exeResult.setStatus(ExecutorStatus.SUCESS);
                exeResult.getCountDownLatch().countDown();
            }

            @Override
            public void onFailure(Throwable t) {
                exeResult.setException(t);
                exeResult.setStatus(ExecutorStatus.FAIL);
                failResultMap.put(index, exeResult);
                resultNum.incrementAndGet();
                exeResult.getCountDownLatch().countDown();
            }
        });
        return index;
    }

    public ArrayList<AsynExecutorResult> getAllResultList() {
        return new ArrayList<>(resultMap.values());
    }

    public ConcurrentHashMap<Integer, AsynExecutorResult> getAllResultMap() {
        return resultMap;
    }

    public ConcurrentHashMap<Integer, AsynExecutorResult> getAllFailResultMap() {
        return failResultMap;
    }

    public AsynExecutorResult getResultByIndex(int index) {
        return resultMap.get(index);
    }

    public ExecutorStatus getExecutorCurrentStatus() {
        Collection<AsynExecutorResult> resultColl = resultMap.values();
        for (AsynExecutorResult result : resultColl) {
            if (result.getStatus() != ExecutorStatus.SUCESS) {
                return result.getStatus();
            }
        }
        return ExecutorStatus.SUCESS;
    }

    public ExecutorStatus getExecutorFinalStatus() {
        if (resultNum.get() != 0) {
            return ExecutorStatus.FAIL;
        }
        Collection<AsynExecutorResult> resultColl = resultMap.values();
        for (AsynExecutorResult result : resultColl) {
            if (result.getStatus() == ExecutorStatus.FAIL) {
                return ExecutorStatus.FAIL;
            } else if (result.getStatus() == ExecutorStatus.RUNNING) {
                try {
                    result.getWithCallBack();
                } catch (Exception e) {
                    logger.debug(String.valueOf(e));
                    return ExecutorStatus.FAIL;
                }
            }
        }
        return ExecutorStatus.SUCESS;
    }

    public void resetResultList() {
        resultMap.clear();
        indexNum = new AtomicInteger(0);
        statusNum = new AtomicInteger(0);
        resultNum = new AtomicInteger(0);
    }
}
