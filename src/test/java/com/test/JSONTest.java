package com.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.stream.Collectors;

public class JSONTest {

    public static List<String> extractSubSealTypes(JSONArray dataArray) {
        return dataArray.stream()
                .map(obj -> ((JSONObject) obj).getString("subSealType"))
                .distinct()
                .collect(Collectors.toList());
    }

    public static void main(String[] args) {
        String jsonData = "{\"data\":[{\"subSealType\":\"50\",\"yPos\":\"0\",\"organFlag\":\"0\",\"fileNo\":\"00010046\",\"keyText\":\"公司盖章\",\"keyNum\":\"1\",\"remark\":\"\",\"sealType\":\"5\",\"xPos\":\"-150\",\"pageNum\":\"0\",\"locateType\":\"2\"},{\"subSealType\":\"50\",\"yPos\":\"0\",\"organFlag\":\"1,3,5,6\",\"fileNo\":\"00010046\",\"keyText\":\"公司盖章\",\"keyNum\":\"1\",\"remark\":\"\",\"sealType\":\"5\",\"xPos\":\"-150\",\"pageNum\":\"0\",\"locateType\":\"2\"},{\"subSealType\":\"51\",\"yPos\":\"0\",\"organFlag\":\"0\",\"fileNo\":\"00010047\",\"keyText\":\"授权签字\",\"keyNum\":\"1\",\"remark\":\"\",\"sealType\":\"5\",\"xPos\":\"10\",\"pageNum\":\"0\",\"locateType\":\"2\"},{\"subSealType\":\"51\",\"yPos\":\"0\",\"organFlag\":\"1,3,5,6\",\"fileNo\":\"00010047\",\"keyText\":\"授权签字\",\"keyNum\":\"1\",\"remark\":\"\",\"sealType\":\"5\",\"xPos\":\"10\",\"pageNum\":\"0\",\"locateType\":\"2\"}],\"page\":{\"totalPages\":1,\"pageSize\":100,\"totalRows\":4,\"currentPage\":1}}";

        // 解析JSON数据
        JSONObject jsonObject = JSON.parseObject(jsonData);
        JSONArray dataArray = jsonObject.getJSONArray("data");
        List<String> subSealTypes = extractSubSealTypes(dataArray);
        System.out.println("去重后的subSealTypes: " + subSealTypes);
    }
}
