package com.angel.model;

public class RequestBean {

    private User user;

    @Override
    public String toString() {
        return "RequestBean{" +
                "user=" + user +
                '}';
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
