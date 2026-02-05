package com.angel.service;

import com.alibaba.fastjson.JSONObject;
import com.angel.entity.RequestVoiceContext;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class VoiceService {
    // 存储每个请求的上下文信息
    private final ConcurrentMap<String, RequestVoiceContext> requestContexts = new ConcurrentHashMap<>();

    @Value("${nbop.voice.path:http://10.128.13.158:8084/iat}")
    private String url;

    @Value("${nbop.voice.appid:pc20onli}")
    private String appId;

    @Value("${nbop.voice.token:test01}")
    private String token;

    public String voiceToText(MultipartFile file) {
        // 生成唯一请求ID
        String requestId = this.generateAuthId();
        // 创建请求上下文
        RequestVoiceContext context = new RequestVoiceContext(requestId);
        requestContexts.put(requestId, context);

        try {
            this.sessionBegin(requestId);
            this.audioWrite(file, requestId);
            this.getResult(requestId);
            this.sessionEnd(requestId);

            return context.getAllResult();
        } catch (Exception e) {
            // 发生异常时清理上下文
            requestContexts.remove(requestId);
            throw e;
        }
    }

    private void sessionBegin(String requestId) {
        RequestVoiceContext context = requestContexts.get(requestId);
        if (context == null) {
            throw new IllegalStateException("Request context not found for requestId: " + requestId);
        }

        try {
            final Base64.Decoder decoder = Base64.getDecoder();
            JSONObject json_params = new JSONObject();
            json_params.put("appid", appId);
            json_params.put("aue", "raw");
            json_params.put("auf", "audio/L16;rate=16000");
            json_params.put("auth_id", context.getAuthId());
            json_params.put("cmd", "ssb");
            json_params.put("extend_params", "{\"params\":\"eos=999999,bos=999999,engine_param=puncproc=true,token=" + token + ",ability=ab_asr\"}");
            json_params.put("svc", "iat");
            json_params.put("syncid", String.valueOf(context.getAndIncrementSyncId()));

            JSONObject json_body = new JSONObject();
            json_body.put("jsonrpc", "2.0");
            json_body.put("method", "deal_request");
            json_body.put("params", json_params);
            json_body.put("id", 1);

            String responseBody = this.sendJsonHttpPost(url, json_body.toJSONString(), requestId);
            String strResponseBody = new String(decoder.decode(responseBody), "UTF-8");
            JSONObject json = JSONObject.parseObject(strResponseBody);
            System.out.println("SessionBegin:" + JSONObject.toJSONString(json, true));

            context.setSid(json.getJSONObject("result").getString("sid"));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("SessionBegin failed", e);
        }
    }

    private void audioWrite(MultipartFile file, String requestId) {
        RequestVoiceContext context = requestContexts.get(requestId);
        if (context == null || context.getSid() == null) {
            throw new IllegalStateException("Invalid request context or SID not initialized");
        }

        try {
            final Base64.Decoder decoder = Base64.getDecoder();
            final Base64.Encoder encoder = Base64.getEncoder();

            ArrayList<byte[]> buffers = this.readMultipartFile(file);

            for (int i = 0; i < buffers.size(); i++) {
                byte[] wave = buffers.get(i);
                int audioStatus;

                if (i == 0) {
                    audioStatus = 1; // 第一段音频
                } else if (i == buffers.size() - 1) {
                    audioStatus = 4; // 最后一段音频
                } else {
                    audioStatus = 2; // 中间音频
                }

                JSONObject json_params = new JSONObject();
                json_params.put("appid", appId);
                json_params.put("audioStatus", String.valueOf(audioStatus));
                json_params.put("auth_id", context.getAuthId());
                json_params.put("cmd", "auw");
                json_params.put("data", encoder.encodeToString(wave));
                json_params.put("extend_params", "{\"params\":\"eos=999999,bos=999999,engine_param=puncproc=true,token=" + token + ",ability=ab_asr\"}");
                json_params.put("sid", context.getSid());
                json_params.put("svc", "iat");
                json_params.put("syncid", String.valueOf(context.getAndIncrementSyncId()));

                JSONObject json_body = new JSONObject();
                json_body.put("jsonrpc", "2.0");
                json_body.put("method", "deal_request");
                json_body.put("params", json_params);
                json_body.put("id", 2);

                String responseBody = this.sendJsonHttpPost(url, json_body.toJSONString(), requestId);
                String strResponseBody = new String(decoder.decode(responseBody), "UTF-8");
                JSONObject json = JSONObject.parseObject(strResponseBody);

                // 判断引擎返回pgs是否为1，为1表示有识别结果可获取
                if (json.getJSONObject("result").getInteger("pgs") == 1) {
                    context.appendResult(json.getJSONObject("result").getString("result"));
                    System.out.println("AudioWrite:" + JSONObject.toJSONString(json, true));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("AudioWrite failed", e);
        }
    }

    private void getResult(String requestId) {
        RequestVoiceContext context = requestContexts.get(requestId);
        if (context == null || context.getSid() == null) {
            throw new IllegalStateException("Invalid request context or SID not initialized");
        }

        try {
            final Base64.Decoder decoder = Base64.getDecoder();

            while (true) {
                JSONObject json_params = new JSONObject();
                json_params.put("appid", appId);
                json_params.put("auth_id", context.getAuthId());
                json_params.put("cmd", "grs");
                json_params.put("extend_params", "{\"params\":\"eos=999999,bos=999999,engine_param=puncproc=true,token=" + token + ",ability=ab_asr\"}");
                json_params.put("sid", context.getSid());
                json_params.put("svc", "iat");
                json_params.put("syncid", String.valueOf(context.getAndIncrementSyncId()));

                JSONObject json_body = new JSONObject();
                json_body.put("jsonrpc", "2.0");
                json_body.put("method", "deal_request");
                json_body.put("params", json_params);
                json_body.put("id", 3);

                String responseBody = this.sendJsonHttpPost(url, json_body.toJSONString(), requestId);
                String strResponseBody = new String(decoder.decode(responseBody), "UTF-8");
                JSONObject json = JSONObject.parseObject(strResponseBody);

                // 判断引擎返回pgs是否为1，为1表示有识别结果可获取
                if (json.getJSONObject("result").getInteger("pgs") == 1) {
                    context.appendResult(json.getJSONObject("result").getString("result"));
                    System.out.println("GetResult:" + JSONObject.toJSONString(json, true));
                }

                if (5 == json.getJSONObject("result").getInteger("recStatus")) {
                    break;
                }
            }

            System.out.println("allResult:" + context.getAllResult());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("GetResult failed", e);
        }
    }

    private void sessionEnd(String requestId) {
        RequestVoiceContext context = requestContexts.get(requestId);
        if (context == null || context.getSid() == null) {
            throw new IllegalStateException("Invalid request context or SID not initialized");
        }

        try {
            final Base64.Decoder decoder = Base64.getDecoder();

            JSONObject json_params = new JSONObject();
            json_params.put("appid", appId);
            json_params.put("auth_id", context.getAuthId());
            json_params.put("cmd", "sse");
            json_params.put("extend_params", "{\"params\":\"eos=999999,bos=999999,engine_param=puncproc=true,token=" + token + ",ability=ab_asr\"}");
            json_params.put("sid", context.getSid());
            json_params.put("svc", "iat");
            json_params.put("syncid", String.valueOf(context.getAndIncrementSyncId()));

            JSONObject json_body = new JSONObject();
            json_body.put("jsonrpc", "2.0");
            json_body.put("method", "deal_request");
            json_body.put("params", json_params);
            json_body.put("id", 4);

            String responseBody = this.sendJsonHttpPost(url, json_body.toJSONString(), requestId);
            String strResponseBody = new String(decoder.decode(responseBody), "UTF-8");
            JSONObject json = JSONObject.parseObject(strResponseBody);
            System.out.println("SessionEnd:" + JSONObject.toJSONString(json, true));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("SessionEnd failed", e);
        } finally {
            // 处理完成后移除上下文
            requestContexts.remove(requestId);
        }
    }

    public String generateAuthId() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789:_";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        // authId定长32
        int randomLength = 32 - timestamp.length();
        StringBuilder randomChars = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < randomLength; i++) {
            int index = random.nextInt(characters.length());
            randomChars.append(characters.charAt(index));
        }

        return timestamp + randomChars.toString();
    }

    private String sendJsonHttpPost(String url, String json, String requestId) {
        RequestVoiceContext context = requestContexts.get(requestId);
        String cookie = context != null ? context.getScookie() : "";

        CloseableHttpResponse response = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Content-type", "application/json-rpc; charset=utf-8");
            httpPost.addHeader("Accept", "application/json-rpc");

            if (cookie != null && !cookie.isEmpty()) {
                httpPost.addHeader("Cookie", cookie);
            }

            httpPost.setEntity(new StringEntity(json, Charset.forName("UTF-8")));

            CloseableHttpClient httpClient = HttpClients.createDefault();
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            int status = response.getStatusLine().getStatusCode();

            String responseInfo = null;
            if (status >= 200 && status < 300) {
                if (entity != null) {
                    responseInfo = EntityUtils.toString(entity);
                }
            }

            // 更新上下文的 cookie
            if (context != null && (cookie == null || cookie.isEmpty())) {
                Header[] headers = response.getHeaders("Set-Cookie");
                if (headers != null && headers.length == 1) {
                    context.setScookie(headers[0].getValue().split(";")[0]);
                }
            }

            return responseInfo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private ArrayList<byte[]> readMultipartFile(MultipartFile file) {
        ArrayList<byte[]> buffers = new ArrayList<>();
        try {
            byte[] fileBytes = file.getBytes();
            int length;
            byte[] bts = new byte[2560];
            int offset = 0;

            while (offset < fileBytes.length) {
                length = Math.min(2560, fileBytes.length - offset);
                byte[] chunk = new byte[length];
                System.arraycopy(fileBytes, offset, chunk, 0, length);
                buffers.add(chunk);
                offset += length;
            }

            if (buffers.isEmpty()) {
                buffers.add(new byte[0]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffers;
    }
}