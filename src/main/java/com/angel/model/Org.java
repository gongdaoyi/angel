package com.angel.model;

import java.util.List;

public class Org {

    private String orgName;

    private String orgCode;

    private List<User> users;

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    @Override
    public String toString() {
        return "Org{" +
                "orgName='" + orgName + '\'' +
                ", orgCode='" + orgCode + '\'' +
                ", users=" + users +
                '}';
    }
}
