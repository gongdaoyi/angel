package com.angel.mapper;

import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CrdtBlackListMapper {

    List<JSONObject> listCrdtBlackList(@Param("clientId") String clientId, @Param("str") String str);
}
