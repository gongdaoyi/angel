package com.angel.model;

import cn.afterturn.easypoi.excel.annotation.Excel;

import java.io.Serializable;

public class ExcelDto implements Serializable {

    @Excel(name = "简称", orderNum = "0")
    private String nationality;

    @Excel(name = "国家/地区", orderNum = "1")
    private String nationalityName;

    @Excel(name = "个人分配", orderNum = "3")
    private String user;

    @Excel(name = "个人长度", orderNum = "4")
    private String userSize;

    @Excel(name = "机构分配", orderNum = "5")
    private String org;

    @Excel(name = "机构长度", orderNum = "6")
    private String orgSize;

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getNationalityName() {
        return nationalityName;
    }

    public void setNationalityName(String nationalityName) {
        this.nationalityName = nationalityName;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUserSize() {
        return userSize;
    }

    public void setUserSize(String userSize) {
        this.userSize = userSize;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getOrgSize() {
        return orgSize;
    }

    public void setOrgSize(String orgSize) {
        this.orgSize = orgSize;
    }
}
