package com.angel.sync;

public enum ExecutorStatus {

    INIT("init"),
    RUNNING("running"),
    SUCESS("sucess"),
    FAIL("fail");
    private final String value;

    ExecutorStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}