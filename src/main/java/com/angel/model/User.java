package com.angel.model;

/**
 * @description:
 * @create: 2020-07-03 16:33
 **/

public class User {

    private String userName;
    private double userAge;
    private String userTel;
    private String clientId;

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", userAge=" + userAge +
                ", userTel='" + userTel + '\'' +
                ", clientId='" + clientId + '\'' +
                '}';
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public double getUserAge() {
        return userAge;
    }

    public void setUserAge(double userAge) {
        this.userAge = userAge;
    }

    public String getUserTel() {
        return userTel;
    }

    public void setUserTel(String userTel) {
        this.userTel = userTel;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
