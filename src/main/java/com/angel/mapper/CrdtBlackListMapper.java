package com.angel.mapper;

import com.alibaba.fastjson.JSONObject;
import com.angel.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CrdtBlackListMapper {

    List<JSONObject> list(@Param("clientId") String clientId);

    int update(@Param("a") User user);
}
