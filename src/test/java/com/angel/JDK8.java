package com.angel;

import com.alibaba.fastjson.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @description: JDK1.8 新特性
 * @create: 2020-07-08 14:59
 **/
public class JDK8 {
    public static void main(String[] args) {
        String params = "1,2,3";
        String[] courseCodes = params.split(",");
        Set<String> courseIdSet = Arrays.stream(courseCodes)
                .map(id -> {
                    String value = getValue(id);
                    JSONObject resJson = JSONObject.parseObject(value);
                    JSONObject dataJson = resJson.getJSONObject("data");
                    return dataJson;
                })
                .filter(Objects::nonNull)
                .map(c -> c.getString("id"))
                .collect(Collectors.toSet());
        System.out.println(courseIdSet);
    }

    public static String getValue(String key) {
        Map<String, String> map = new HashMap<>();
        map.put("1", "{\"data\":{\"id\":\"一号\"}}");
        map.put("2", "{\"data\":{\"id\":\"二号\"}}");
        map.put("3", "{\"data\":{\"id\":\"三号\"}}");
        return map.get(key);
    }
}
