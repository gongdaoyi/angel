package com.angel.entity;

import java.util.concurrent.atomic.AtomicInteger;

public class RequestVoiceContext {
    private final AtomicInteger syncId = new AtomicInteger(0);
    private final String authId;
    private String sid = "";
    private String cookie = "";
    private String allResult = "";

    public RequestVoiceContext(String authId) {
        this.authId = authId;
    }

    public String getAuthId() {
        return authId;
    }

    public int getAndIncrementSyncId() {
        return syncId.getAndIncrement();
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getScookie() {
        return cookie;
    }

    public void setScookie(String cookie) {
        this.cookie = cookie;
    }

    public String getAllResult() {
        return allResult;
    }

    public void appendResult(String result) {
        this.allResult += result;
    }
}
