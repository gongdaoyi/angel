package com.angel.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * OCR 数据匹配服务实现类，用于将 OCR 返回的非结构化文本数据转换为结构化的 JSON 对象。
 * 支持正则匹配、递归解析和数据替换功能。
 */
@Service
public class MatcherServiceImpl {

    private static final Logger log = LoggerFactory.getLogger(MatcherServiceImpl.class);

    @Value("${ocr.recognize.info}")
    private String info;

    @Value("${ocr.recognize.regex}")
    private String regex;

    @Value("${ocr.recognize.response}")
    private String response;

    /**
     * 主方法：处理 OCR 返回的数据并转换为结构化 JSON 对象。
     *
     * @return JSONObject 结构化的数据结果
     * @throws IOException 如果读取 OCR 数据失败
     */
    public JSONObject archOcr() throws IOException {
        // 1. 处理 OCR 返回的原始数据
        String data = handleOCRResult();
        log.info(data);

        // 2. 对数据进行正则匹配和结构化处理
        return this.matchedData(data);
    }

    /**
     * 对 OCR 数据进行正则匹配和结构化处理。
     *
     * @param data OCR 返回的文本数据
     * @return JSONObject 结构化的匹配结果
     */
    private JSONObject matchedData(String data) {
        JSONObject result = new JSONObject();
        // 1. 解析正则表达式和响应模板
        JSONObject patternJson = parseJsonSafely(regex);
        JSONObject patternName = parseJsonSafely(response);

        // 2. 使用根正则表达式匹配数据
        Matcher matcherRoot = Pattern.compile(patternJson.getString("root")).matcher(data);

        if (matcherRoot.find()) {
            // 3. 如果匹配成功，递归解析嵌套结构
            recursionMarcher(result, patternJson, patternName, matcherRoot);
        } else {
            // 4. 如果匹配失败，返回默认响应模板
            result = patternName;
            return recursionResultJSON(result, patternName);
        }

        return result;
    }

    private JSONObject parseJsonSafely(String jsonStr) {
        try {
            return JSON.parseObject(jsonStr);
        } catch (JSONException e) {
            log.error("Failed to parse JSON: {}", jsonStr, e);
            throw new IllegalArgumentException("Invalid JSON format", e);
        }
    }

    /**
     * 递归匹配和解析嵌套的 JSON 结构。
     *
     * @param resultJson  结果 JSON 对象
     * @param matcherJson 正则表达式配置
     * @param paramJson   参数 JSON 对象
     * @param matcher     正则匹配器
     * @return JSONObject 递归解析后的结果
     */
    private JSONObject recursionMarcher(JSONObject resultJson, JSONObject matcherJson, JSONObject paramJson, Matcher matcher) {
        // 遍历参数 JSON 的每个字段
        for (Map.Entry<String, Object> entry : paramJson.entrySet()) {
            String key = entry.getKey();
            String group = matcher.group(key);

            String value = String.valueOf(entry.getValue());
            // 如果值为空或非 JSON，直接放入结果
            if ("".equals(value) || !this.isJson(value)) {
                resultJson.put(key, group);
                continue;
            }

            // 获取子正则表达式并匹配
            String matcherKey = matcherJson.getString(key);
            Matcher matcherSon = Pattern.compile(matcherKey).matcher(group);

            JSONArray propertyArray = new JSONArray();
            JSONObject valueJson;
            boolean isCyclic = false;
            try {
                // 尝试解析为 JSON 对象
                valueJson = JSON.parseObject(value);
            } catch (JSONException e) {
                // 如果解析失败，尝试解析为 JSON 数组
                JSONArray successorArray = JSON.parseArray(value);
                valueJson = (JSONObject) successorArray.get(0);
                isCyclic = true;
            }

            if (isCyclic) {
                // 处理循环结构（数组）
                if (matcherSon.find()) {
                    int num = 0;
                    while (matcherSon.find(num)) {
                        JSONObject propertyJson = new JSONObject();
                        // 填充子字段
                        for (Map.Entry<String, Object> lowEntry : valueJson.entrySet()) {
                            String lowKey = lowEntry.getKey();
                            propertyJson.put(lowKey, matcherSon.group(lowKey));
                        }
                        propertyArray.add(propertyJson);
                        num = matcherSon.end();
                    }
                }
                resultJson.put(key, propertyArray);
            } else {
                // 处理普通 JSON 对象
                JSONObject propertyJson = new JSONObject();
                if (matcherSon.find()) {
                    // 递归解析子结构
                    propertyJson = this.recursionMarcher(new JSONObject(), matcherJson, valueJson, matcherSon);
                } else {
                    // 直接匹配子字段
                    for (Map.Entry<String, Object> lowEntry : valueJson.entrySet()) {
                        String lowKey = lowEntry.getKey();
                        String lowKeyMatcher = matcherJson.getString(lowKey);
                        Matcher propertyMatcher = Pattern.compile(lowKeyMatcher).matcher(group);
                        propertyJson.put(lowKey, propertyMatcher.find() ? propertyMatcher.group(lowKey) : "");
                    }
                }
                resultJson.put(key, propertyJson);
            }
        }
        return resultJson;
    }

    private JSONObject recursionResultJSON(JSONObject resultJson, JSONObject paramJson) {

        for (Map.Entry<String, Object> entry : paramJson.entrySet()) {
            String key = entry.getKey();
            String value = String.valueOf(entry.getValue());

            if (!"".equals(value) && isJson(value)) {
                if (isJsonArray(value)) {
                    JSONArray resValueArray = new JSONArray();
                    resultJson.put(key, resValueArray);
                    continue;
                }

                JSONObject valueJson = JSON.parseObject(value);
                JSONObject resValue = new JSONObject();
                recursionResultJSON(resValue, valueJson);

                resultJson.put(key, resValue);
            } else {
                resultJson.put(key, "");
            }
        }

        return resultJson;
    }

    private boolean isJsonArray(String value) {
        try {
            JSON.parseArray(value);
            return true;
        } catch (JSONException e) {
            return false;
        }
    }

    private boolean isJson(String value) {
        return isJsonObject(value) || isJsonArray(value);
    }

    private boolean isJsonObject(String value) {
        try {
            JSON.parseObject(value);
            return true;
        } catch (JSONException e) {
            return false;
        }
    }

    /**
     * 处理OCR识别返回值, 因为多页和单页的返回结果不一样 需要抓取出有用的数据进行正则匹配
     */
    private String handleOCRResult() throws IOException {
        JSONObject ocrInfo = JSON.parseObject(info);

        boolean isMultiPage = "Y".equals(ocrInfo.getString("multiPage"));

        String output;

        if (!isMultiPage) {
            JSONObject ocrRes = new JSONObject();
            JSONArray linesText = ocrRes.getJSONArray("linesText");
            output = linesText
                    .stream()
                    .map(Object::toString)
                    .collect(Collectors.joining());
        } else {
            Integer begPage = ocrInfo.getInteger("begPage");
            Integer endPage = ocrInfo.getInteger("endPage");

            JSONObject ocrRes = new JSONObject();
            JSONArray pages = ocrRes.getJSONArray("pages");
            output = pages
                    .stream()
                    .filter(v -> {
                        JSONObject data = (JSONObject) JSONObject.toJSON(v);
                        Integer pageNumber = data.getInteger("page_number");

                        return (begPage == null || begPage == 0 || pageNumber >= begPage) && (endPage == null || endPage == 0 || pageNumber <= endPage);
                    })
                    .map(v -> {
                        JSONObject data = (JSONObject) JSONObject.toJSON(v);
                        JSONArray linesText = data.getJSONArray("linesText");

                        return linesText.stream().map(Object::toString).collect(Collectors.joining());
                    })
                    .collect(Collectors.joining());
        }

        if (ocrInfo.containsKey("replace")) {
            output = replaceProcess(output, ocrInfo.getJSONObject("replace"));
        }

        return output;
    }

    private String replaceProcess(String data, JSONObject replaceInfo) {
        String output = data;
        for (String key : replaceInfo.keySet()) {
            output = output.replaceAll(key, replaceInfo.getString(key));
        }

        return output;
    }

}
