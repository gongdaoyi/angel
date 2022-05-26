package com.angel.sync;

import com.google.common.util.concurrent.ListenableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AsynExecutorResult<T> {
    private static final Logger logger = LoggerFactory.getLogger(AsynExecutorResult.class);
    private final ListenableFuture<T> custTask;
    private final CountDownLatch countDownLatch = new CountDownLatch(1);
    private T result;
    private volatile ExecutorStatus status = ExecutorStatus.INIT;
    private Throwable exception;

    public AsynExecutorResult(ListenableFuture<T> custTask) {
        this.custTask = custTask;
    }

    public T get() throws InterruptedException, ExecutionException {
        return custTask.get();
    }

    public T get(long timeout, TimeUnit unit) throws TimeoutException, InterruptedException, ExecutionException {
        return custTask.get(timeout, unit);
    }

    public void getWithCallBack() throws InterruptedException, ExecutionException {
        T t = custTask.get();
        boolean callBackResult = countDownLatch.await(500, TimeUnit.MILLISECONDS);
        if (!callBackResult) {
            logger.warn("callBackResult is false!");
        }
    }

    public T getWithCallBack(long timeout, TimeUnit unit) throws TimeoutException, InterruptedException, ExecutionException {
        T t = custTask.get(timeout, unit);
        countDownLatch.await(timeout, unit);
        return t;
    }


    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public ExecutorStatus getStatus() {
        return status;
    }

    public void setStatus(ExecutorStatus status) {
        this.status = status;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    @Override
    public String toString() {
        return "{ ExecutorStatus: " + status + ",  exception: " + exception + "}";
    }

    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }
}