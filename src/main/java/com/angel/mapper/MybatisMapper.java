package com.angel.mapper;

import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MybatisMapper {

    JSONObject qryNationalityDict(@Param("subentry") String subentry);

}
