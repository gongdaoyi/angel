package com.angel.sync;

@FunctionalInterface
public interface CustCallable<V> {
    V call() throws Exception;
}

