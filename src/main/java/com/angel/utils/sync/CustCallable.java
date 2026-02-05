package com.angel.utils.sync;

@FunctionalInterface
public interface CustCallable<V> {
    V call() throws Exception;
}

