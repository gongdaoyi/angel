package com.angel.mapper;

import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DictMapper {

    JSONObject qryDict(@Param("scanArchNo") String scanArchNo);

}
