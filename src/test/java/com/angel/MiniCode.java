package com.angel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.angel.config.MiniCodeConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class MiniCode {

    /**
     * Write a program to convert the digits from 0 to 9 into letters
     */
    public String numberToLetters(List<String> list) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        List<String> resList = this.handleNumber(list);

        return String.join(" ", resList);
    }

    /**
     * The program need to support converting the digits from 0 to 99 into letters
     */
    public String translationNumber(int param) {
        if (param == 1 || param == 0) {
            return null;
        }

        List<String> numList = new ArrayList<>();
        String strParam = String.valueOf(param);
        if (strParam.length() > 1) {
            numList.add(strParam.substring(0, 1));
            numList.add(strParam.substring(strParam.length() - 1));
        }

        List<String> resList = this.handleNumber(numList);
        return String.join("", resList);
    }


    private List<String> handleNumber(List<String> list) {
        List<String> resList = new ArrayList<>();

        list.forEach(v -> {
            JSONObject data = JSONObject.parseObject(MiniCodeConstant.MIMI_CODE_DATA);
            log.info("MiniCode: {}", data);

            JSONArray jsonArray = data.getJSONArray(v);

            if (!CollectionUtils.isEmpty(jsonArray)) {
                StringBuilder sbRes = new StringBuilder();

                jsonArray.forEach(obj -> {
                    JSONObject vObj = (JSONObject) JSON.toJSON(obj);
                    sbRes.append(vObj.getString("value"));
                });

                resList.add(sbRes.toString());
            }

        });

        return resList;
    }

}
