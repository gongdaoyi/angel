package com.angel.service;

import com.alibaba.fastjson.JSONObject;
import com.angel.model.User;

import java.util.List;

public interface ICrdtBlackListService {

    List<JSONObject> listCrdtBlackList(String clientId);

    int update(User user);
}
