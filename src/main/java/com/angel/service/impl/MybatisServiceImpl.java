package com.angel.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.angel.mapper.MybatisMapper;
import com.angel.service.IMybatisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MybatisServiceImpl implements IMybatisService {

    @Autowired
    MybatisMapper mybatisMapper;

    @Override
    public JSONObject qryNationalityDict(String subentry) {
        return mybatisMapper.qryNationalityDict(subentry);
    }

}
