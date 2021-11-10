//package com.angel.nbop;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONException;
//import com.alibaba.fastjson.JSONObject;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Scope;
//import org.springframework.core.io.ByteArrayResource;
//import org.springframework.core.io.FileSystemResource;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Service;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.client.RestTemplate;
//
//import java.io.File;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//import java.util.stream.Collectors;
//
//
//@Scope("prototype")
//@Service
//public class FileServer {
//
//    @Autowired
//    RelationS3Services relationS3Services;
//
//    public byte[] downLoadS3File(String bucketName, String objectName) {
//
//        if (objectName.contains("~~")) {
//            objectName = objectName.replace("~~", "/");
//        }
//
//        return relationS3Services.s3_get_obj(bucketName, objectName);
//
//
//    }
//
//    private String handleOCR(String filePath, JSONObject ocrInfo) {
//
//        boolean isMultiPage = "Y".equals(ocrInfo.getString("multiPage"));
//
//        String output;
//
//        if (!isMultiPage) {
//            JSONObject ocrRes = this.sendFile(filePath, "OcrUrl");
//            JSONArray linesText = ocrRes.getJSONArray("linesText");
//            output = linesText
//                    .stream()
//                    .map(Object::toString)
//                    .collect(Collectors.joining());
//        } else {
//            Integer begPage = ocrInfo.getInteger("begPage");
//            Integer endPage = ocrInfo.getInteger("endPage");
//
//            JSONObject ocrRes = this.sendFile(filePath, "OcrListUrl");
//            JSONArray pages = ocrRes.getJSONArray("pages");
//            output = pages
//                    .stream()
//                    .filter(v -> {
//                        JSONObject data = (JSONObject) JSONObject.toJSON(v);
//                        Integer pageNumber = data.getInteger("page_number");
//
//                        return (begPage == null || begPage == 0 || pageNumber >= begPage) && (endPage == null || endPage == 0 || pageNumber <= endPage);
//                    })
//                    .map(v -> {
//                        JSONObject data = (JSONObject) JSONObject.toJSON(v);
//                        JSONArray linesText = data.getJSONArray("linesText");
//
//                        return linesText.stream().map(Object::toString).collect(Collectors.joining());
//                    })
//                    .collect(Collectors.joining());
//        }
//
//        if (ocrInfo.containsKey("replace")) {
//            output = replaceProcess(output, ocrInfo.getJSONObject("replace"));
//        }
//
//        return output;
//    }
//
//    private JSONObject sendFile(String filePath, String ocrUrl) {
//        MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
//        Map<String, Object> inputDataMap = new HashMap<>();
//
//        if (filePath.contains("downLoadS3File")) {
//            String[] split = filePath.split("/");
//            String bucketName = split[1];
//            String s3FilePath = split[2];
//
//            byte[] fileBytes = this.downLoadS3File(bucketName, s3FilePath);
//
//            ByteArrayResource byteArrayResource = new ByteArrayResource(fileBytes) {
//                @Override
//                public String getFilename() {
//                    return "file";
//                }
//            };
//
//            inputDataMap.put("file", byteArrayResource);
//        } else {
//            inputDataMap.put("file", new FileSystemResource(new File(filePath)));
//        }
//
//        multiValueMap.setAll(inputDataMap);
//        ;
//
//        HttpHeaders headers = new HttpHeaders();
//        MediaType type = MediaType.parseMediaType("multipart/form-data;charset=UTF-8");
//        headers.setContentType(type);
//        JSONObject json = new RestTemplate().postForObject(ocrUrl, multiValueMap, JSONObject.class);
//
//        assert json != null;
//        return json.getJSONObject("data");
//    }
//
//    private String replaceProcess(String data, JSONObject replaceInfo) {
//        String output = data;
//        for (String key : replaceInfo.keySet()) {
//            output = output.replaceAll(key, replaceInfo.getString(key));
//        }
//
//        return output;
//    }
//
//    private JSONObject matchedData(String data, String patternStr, JSONObject patternName) {
//
//        JSONObject patternJson = (JSONObject) JSON.parse(patternStr);
//        JSONObject result = new JSONObject();
//
//        Matcher matcherRoot = Pattern.compile(patternJson.getString("root")).matcher(data);
//
//        if (matcherRoot.find()) {
//            this.recursionMarcher(result, patternJson, patternName, matcherRoot);
//        } else {
//            result = patternName;
//            this.recursionResultJSON(result, patternName);
//        }
//
//        return result;
//    }
//
//    private JSONObject recursionMarcher(JSONObject resultJson, JSONObject matcherJson, JSONObject paramJson, Matcher matcher) {
//        for (Map.Entry<String, Object> entry : paramJson.entrySet()) {
//            String key = entry.getKey();
//            String group = matcher.group(key);
//
//            String value = String.valueOf(entry.getValue());
//            if ("".equals(value) || !this.isJson(value)) {
//                resultJson.put(key, group);
//                continue;
//            }
//
//            String matcherKey = matcherJson.getString(key);
//            Matcher matcherSon = Pattern.compile(matcherKey).matcher(group);
//
//            JSONArray propertyArray = new JSONArray();
//            JSONObject valueJson;
//            boolean isCyclic = false;
//            try {
//                valueJson = JSON.parseObject(value);
//            } catch (JSONException e) {
//                JSONArray successorArray = JSON.parseArray(value);
//                valueJson = (JSONObject) successorArray.get(0);
//                isCyclic = true;
//            }
//            if (isCyclic) {
//                if (matcherSon.find()) {
//                    int num = 0;
//                    while (matcherSon.find(num)) {
//                        JSONObject propertyJson = new JSONObject();
//
//                        for (Map.Entry<String, Object> lowEntry : valueJson.entrySet()) {
//                            String lowKey = lowEntry.getKey();
//                            propertyJson.put(lowKey, matcherSon.group(lowKey));
//                        }
//                        propertyArray.add(propertyJson);
//                        num = matcherSon.end();
//                    }
//                }
//
//                resultJson.put(key, propertyArray);
//            } else {
//                JSONObject propertyJson = new JSONObject();
//                if (matcherSon.find()) {
//                    propertyJson = this.recursionMarcher(new JSONObject(), matcherJson, valueJson, matcherSon);
//                } else {
//                    for (Map.Entry<String, Object> lowEntry : valueJson.entrySet()) {
//                        String lowKey = lowEntry.getKey();
//
//                        String lowKeyMatcher = matcherJson.getString(lowKey);
//                        Matcher propertyMatcher = Pattern.compile(lowKeyMatcher).matcher(group);
//
//                        if (propertyMatcher.find()) {
//                            propertyJson.put(lowKey, propertyMatcher.group(lowKey));
//                        } else {
//                            propertyJson.put(lowKey, "");
//                        }
//                    }
//                }
//
//                resultJson.put(key, propertyJson);
//            }
//        }
//        return resultJson;
//    }
//
//    private JSONObject recursionResultJSON(JSONObject resultJson, JSONObject paramJson) {
//
//        for (Map.Entry<String, Object> entry : paramJson.entrySet()) {
//            String key = entry.getKey();
//            String value = String.valueOf(entry.getValue());
//
//            if (!"".equals(value) && isJson(value)) {
//                if (isJsonArray(value)) {
//                    JSONArray resValueArray = new JSONArray();
//                    resultJson.put(key, resValueArray);
//                    continue;
//                }
//
//                JSONObject valueJson = JSON.parseObject(value);
//                JSONObject resValue = new JSONObject();
//                recursionResultJSON(resValue, valueJson);
//
//                resultJson.put(key, resValue);
//            } else {
//                resultJson.put(key, "");
//            }
//        }
//
//        return resultJson;
//    }
//
//    private boolean isJsonArray(String value) {
//        boolean isJsonArray = true;
//        try {
//            JSON.parseArray(value);
//        } catch (JSONException e) {
//            isJsonArray = false;
//        }
//        return isJsonArray;
//    }
//
//    private boolean isJson(String value) {
//        boolean isJson = true;
//        try {
//            JSON.parseObject(value);
//        } catch (JSONException json) {
//            try {
//                JSON.parseArray(value);
//                return true;
//            } catch (JSONException array) {
//                isJson = false;
//            }
//        }
//        return isJson;
//    }
//
//}