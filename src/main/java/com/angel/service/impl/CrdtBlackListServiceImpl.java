package com.angel.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.angel.mapper.CrdtBlackListMapper;
import com.angel.service.ICrdtBlackListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CrdtBlackListServiceImpl implements ICrdtBlackListService {

    @Autowired
    private CrdtBlackListMapper crdtBlackListMapper;

    @Override
    public List<JSONObject> listCrdtBlackList(String clientId, String str) {
        return crdtBlackListMapper.listCrdtBlackList(clientId, str);
    }
}
