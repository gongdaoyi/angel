package com.angel.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/host")
public class ConvertController {

    /**
     * 批量处理媒体转换进度
     * 从ConvertInventory.txt文件读取每一行作为filePath，循环处理直到所有文件处理完毕
     *
     * @return 批量处理结果
     */
    @PostMapping("/getMediaConvertProgress")
    public ResponseEntity<String> getMediaConvertProgress() {
        try {
            // 从资源文件读取文件路径列表
            List<String> filePaths = readFilePathsFromFile();
            log.info("从ConvertInventory.txt读取到{}个文件路径", filePaths.size());

            // 准备处理结果统计
            JSONObject batchResult = new JSONObject();
            batchResult.put("totalFiles", filePaths.size());
            batchResult.put("processedFiles", 0);
            batchResult.put("successFiles", 0);
            batchResult.put("failedFiles", 0);
            batchResult.put("skippedFiles", 0);

            JSONArray resultDetails = new JSONArray();

            // 循环处理每个文件路径
            for (int i = 0; i < filePaths.size(); i++) {
                String filePath = filePaths.get(i);
                log.info("正在处理第{}行，文件路径: {}", (i + 1), filePath);

                try {
                    // 处理单个文件
                    JSONObject singleResult = processSingleFile(filePath);
                    singleResult.put("lineNumber", i + 1);
                    singleResult.put("filePath", filePath);
                    resultDetails.add(singleResult);

                    // 更新统计信息
                    batchResult.put("processedFiles", i + 1);

                    if (singleResult.getBooleanValue("success")) {
                        batchResult.put("successFiles", batchResult.getIntValue("successFiles") + 1);
                    } else if ("转换失败: 缺结束标识".equals(singleResult.getString("message"))) {
                        batchResult.put("skippedFiles", batchResult.getIntValue("skippedFiles") + 1);
                    } else {
                        batchResult.put("failedFiles", batchResult.getIntValue("failedFiles") + 1);
                    }

                    log.info("第{}行处理完成，当前进度: {}/{} (成功: {}, 失败: {}, 跳过: {})",
                            (i + 1), (i + 1), filePaths.size(),
                            batchResult.getIntValue("successFiles"),
                            batchResult.getIntValue("failedFiles"),
                            batchResult.getIntValue("skippedFiles"));

                } catch (Exception e) {
                    // 处理单个文件时的异常
                    JSONObject errorResult = new JSONObject();
                    errorResult.put("lineNumber", i + 1);
                    errorResult.put("filePath", filePath);
                    errorResult.put("success", false);
                    errorResult.put("error", e.getMessage());
                    resultDetails.add(errorResult);

                    // 更新统计信息
                    batchResult.put("processedFiles", i + 1);
                    batchResult.put("failedFiles", batchResult.getIntValue("failedFiles") + 1);

                    log.error("第{}行处理失败，文件路径: {}, 错误: {}", (i + 1), filePath, e.getMessage());
                }
            }

            // 添加处理结果详情
            batchResult.put("details", resultDetails);

            return ResponseEntity.ok(batchResult.toJSONString());

        } catch (Exception e) {
            log.error("批量处理媒体转换进度失败", e);
            return ResponseEntity.status(500).body("批量处理失败: " + e.getMessage());
        }
    }

    /**
     * 从ConvertInventory.txt文件读取文件路径列表
     *
     * @return 文件路径列表
     */
    private List<String> readFilePathsFromFile() throws IOException {
        ClassPathResource resource = new ClassPathResource("ConvertInventory.txt");
        byte[] fileData = FileCopyUtils.copyToByteArray(resource.getInputStream());
        String content = new String(fileData, StandardCharsets.UTF_8);

        // 按行分割内容，并过滤掉空行
        String[] lines = content.split("\\r?\\n");
        List<String> filePaths = new ArrayList<>();

        for (String line : lines) {
            String trimmedLine = line.trim();
            if (!trimmedLine.isEmpty()) {
                filePaths.add(trimmedLine);
            }
        }

        return filePaths;
    }

    /**
     * 处理单个文件的媒体转换
     *
     * @param filePath 文件路径
     * @return 处理结果
     */
    private JSONObject processSingleFile(String filePath) {
        try {
            // 第一步：创建转码任务
            String createConvert = "/media/convert";
            JSONObject createResult = this.callConvert(filePath, createConvert);
            log.info("创建转码任务返回{}", createResult);

            // 检查创建任务是否成功
            if (createResult == null || !createResult.getBooleanValue("success")) {
                JSONObject failureResult = new JSONObject();
                failureResult.put("success", false);
                failureResult.put("message", "创建转码任务失败");
                failureResult.put("createResult", createResult);
                return failureResult;
            }

            // 第二步：循环查询进度
            String qryUrl = "/media/getProgress";
            JSONObject finalResult = null;
            int maxAttempts = 30; // 最大尝试次数，避免无限循环
            int attempt = 0;
            long interval = 5000; // 查询间隔，5秒

            while (attempt < maxAttempts) {
                attempt++;
                log.info("文件: {}, 第{}次查询转换进度", filePath, attempt);

                JSONObject qryResult = this.callConvert(filePath, qryUrl);
                log.info("文件: {}, 查询返回{}", filePath, qryResult);

                // 检查查询是否成功
                if (qryResult != null && qryResult.getBooleanValue("success")) {
                    JSONObject data = qryResult.getJSONObject("data");
                    if (data != null) {
                        double progress = data.getDoubleValue("progress");

                        // 检查进度是否完成
                        if (progress >= 1.0) {
                            finalResult = qryResult;
                            log.info("文件: {}, 转换已完成，进度: {}", filePath, progress);
                            break;
                        }

                        log.info("文件: {}, 当前进度: {}%", filePath, progress * 100);
                    }
                } else {
                    log.warn("文件: {}, 查询进度失败: {}", filePath, qryResult);

                    // 如果查询失败，检查是否是错误码-1且errorInfo包含"缺结束标识"
                    if (qryResult != null &&
                            "-1".equals(qryResult.getString("errorNo")) &&
                            qryResult.getString("errorInfo") != null &&
                            qryResult.getString("errorInfo").contains("缺结束标识")) {

                        log.error("文件: {}, 转换失败(缺结束标识)，停止查询并处理下一个文件", filePath);
                        JSONObject errorResult = new JSONObject();
                        errorResult.put("success", false);
                        errorResult.put("message", "转换失败: 缺结束标识");
                        errorResult.put("errorInfo", qryResult.getString("errorInfo"));
                        errorResult.put("errorNo", qryResult.getString("errorNo"));
                        errorResult.put("attempts", attempt);
                        return errorResult;
                    }
                }

                // 如果不是最后一次尝试，则等待
                if (attempt < maxAttempts) {
                    try {
                        Thread.sleep(interval);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        log.error("文件: {}, 查询进度被中断", filePath, e);
                        JSONObject interruptedResult = new JSONObject();
                        interruptedResult.put("success", false);
                        interruptedResult.put("message", "查询进度被中断");
                        return interruptedResult;
                    }
                }
            }

            // 检查是否在最大尝试次数内完成
            if (finalResult == null) {
                log.warn("文件: {}, 在{}次尝试后，转换仍未完成", filePath, maxAttempts);
                JSONObject timeoutResult = new JSONObject();
                timeoutResult.put("success", false);
                timeoutResult.put("message", "查询超时，转换可能在后台继续进行");
                timeoutResult.put("attempts", attempt);
                return timeoutResult;
            }

            JSONObject successResult = new JSONObject();
            successResult.put("success", true);
            successResult.put("message", "转换成功");
            successResult.put("result", finalResult);
            successResult.put("attempts", attempt);

            return successResult;

        } catch (Exception e) {
            log.error("文件: {}, 处理媒体转换进度失败", filePath, e);
            JSONObject errorResult = new JSONObject();
            errorResult.put("success", false);
            errorResult.put("message", "处理失败");
            errorResult.put("error", e.getMessage());
            return errorResult;
        }
    }

    private JSONObject callConvert(String filePath, String path) {
        try {
            // 构建请求URL和请求体
            String url = "https://nbopuat.gf.com.cn/api/nbop/mediaconvert/1.0.0" + path;
            String md5String = "68a388690883d6f1df4ab65d027309e7";

            // 创建请求体JSON
            JSONObject requestBody = new JSONObject();
            requestBody.put("filePath", filePath);

            // 使用Spring的RestTemplate发送HTTP请求
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("gftrademd5string", md5String);

            HttpEntity<String> entity = new HttpEntity<>(requestBody.toJSONString(), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            return JSONObject.parseObject(response.getBody());
        } catch (Exception e) {
            log.error("查询媒体转换进度失败", e);
            return null;
        }
    }
}