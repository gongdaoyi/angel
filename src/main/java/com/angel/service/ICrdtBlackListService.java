package com.angel.service;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

public interface ICrdtBlackListService {

    List<JSONObject> listCrdtBlackList(String clientId, String str);

}
