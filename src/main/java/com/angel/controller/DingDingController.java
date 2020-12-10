package com.angel.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.angel.config.DingDingConstant;
import com.angel.event.ProcessEvent;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.*;
import com.dingtalk.api.response.*;
import com.dingtalk.oapi.lib.aes.DingTalkEncryptor;
import com.taobao.api.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static com.dingtalk.api.request.OapiProcessinstanceCreateRequest.FormComponentValueVo;

/**
 * 钉钉审批控制器
 * @author HP
 */
@RestController
@RequestMapping(value = "/process")
public class DingDingController {

    private static final Logger logger = LoggerFactory.getLogger(DingDingController.class);

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    /**
     * 钉钉审批回调接口
     */
    @RequestMapping(value = "/callback", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> callback(@RequestParam(required = false) String signature,
                                        @RequestParam(required = false) String timestamp,
                                        @RequestParam(required = false) String nonce,
                                        @RequestBody(required = false) JSONObject body) {
        logger.info("signature={}, timestamp={}, nonce={}, json={}", signature, timestamp, nonce, body);
        try {
            // 获取回调信息的加密数据进行解密处理
            String encryptMsg = body.getString("encrypt");
            DingTalkEncryptor dingTalkEncryptor = new DingTalkEncryptor(DingDingConstant.TOKEN,
                    DingDingConstant.ENCODING_AES_KEY,
                    DingDingConstant.CORP_ID);
            String plainText = dingTalkEncryptor.getDecryptMsg(signature, timestamp, nonce, encryptMsg);

            logger.info("收到审批实例状态更新plainText: " + plainText);
            JSONObject obj = JSON.parseObject(plainText);

            // 目前只处理审批实例的回调事件
            String eventType = obj.getString("EventType");
            if (DingDingConstant.BPMS_INSTANCE_CHANGE.equals(eventType)) {
                // 审批终止时，推送事件。审批开始时，无需推送。
                String type = obj.getString("type");
                // finish 是审批正常结束(同意或者拒绝);  terminate 是审批终止(撤销等非正常终止情况)
                if ("finish".equals(type) || "terminate".equals(type)) {
                    String processCode = obj.getString("processCode");
                    String processInstanceId = obj.getString("processInstanceId");
                    String result = obj.getString("result");

                    // 发起事件通知
                    ProcessEvent event = new ProcessEvent(this);
                    event.setProcessCode(processCode);
                    event.setProcessInstanceId(processInstanceId);
                    event.setResult(result);
                    applicationEventPublisher.publishEvent(event);
                }
            }

            // 返回lion的加密信息表示回调处理成功
            Map<String, String> res = dingTalkEncryptor.getEncryptedMap(DingDingConstant.CALLBACK_RESPONSE_SUCCESS, Long.valueOf(timestamp), nonce);
            logger.info("返回加密信息给钉钉={}", res);

            return res;
        } catch (Exception e) {
            logger.error("process callback failed！", e);
            return null;
        }
    }

    /**
     * 获取调用钉钉接口token
     */
    public String getToken() {
        try {
            DefaultDingTalkClient client = new DefaultDingTalkClient(DingDingConstant.URL_GET_TOKEN);
            OapiGettokenRequest request = new OapiGettokenRequest();

            request.setAppkey(DingDingConstant.APP_KEY);
            request.setAppsecret(DingDingConstant.APP_SECRET);
            request.setHttpMethod("GET");

            OapiGettokenResponse rsp = client.execute(request);
            String body = rsp.getBody();
            logger.info("调用获取getToken接口返回数据={}", body);
            String token = rsp.getAccessToken();
            logger.info("token={}", token);
            return token;
        } catch (ApiException e) {
            logger.error("getToken failed!", e);
        }
        return null;
    }

    /**
     * 根据用户手机号码获取钉钉userId
     */
    public String getByTel(String telephone) {
        try {
            DingTalkClient client = new DefaultDingTalkClient(DingDingConstant.GET_BY_MOBILE);
            OapiUserGetByMobileRequest req = new OapiUserGetByMobileRequest();

            req.setMobile(telephone);
            req.setHttpMethod("GET");
            String token = this.getToken();

            OapiUserGetByMobileResponse rsp = client.execute(req, token);
            String body = rsp.getBody();
            logger.info("getByTel获取用户信息返回数据={}", body);
            String userId = rsp.getUserid();
            logger.info("userId={}", userId);

            return userId;
        } catch (ApiException e) {
            logger.error("getByTel failed!", e);
        }
        return null;
    }

    /**
     * 根据钉钉 UserId获取部门Id
     */
    private Long getDepId(String userId) {
        try {
            DingTalkClient client = new DefaultDingTalkClient(DingDingConstant.URL_USER_GET);
            OapiUserGetRequest request = new OapiUserGetRequest();

            request.setUserid(userId);
            request.setHttpMethod("GET");
            String token = this.getToken();

            OapiUserGetResponse response = client.execute(request, token);
            logger.info("getDepId获取用户详情={}", response.getBody());

            List<Long> departments = response.getDepartment();
            logger.info("departments={}", departments);

            if (CollectionUtils.isEmpty(departments)) {
                return null;
            }

            return departments.get(0);
        } catch (ApiException e) {
            logger.error("getDepId failed!", e);
        }
        return null;
    }

    /**
     * 注册业务事件回调接口 register_call_back
     */
    @GetMapping("/loginCallBack")
    private void loginCallBack() throws ApiException {
        //获取token
        DefaultDingTalkClient client = new DefaultDingTalkClient(DingDingConstant.URL_GET_TOKEN);
        OapiGettokenRequest request = new OapiGettokenRequest();

        request.setAppkey(DingDingConstant.APP_KEY);
        request.setAppsecret(DingDingConstant.APP_SECRET);
        request.setHttpMethod("GET");

        OapiGettokenResponse rsp = client.execute(request);
        String body = rsp.getBody();
        logger.info("调用获取token接口返回数据={}", body);
        String token = rsp.getAccessToken();
        logger.info("获取token={}", token);

        // 重新为企业注册回调
        client = new DefaultDingTalkClient(DingDingConstant.REGISTER_CALLBACK);
        OapiCallBackRegisterCallBackRequest registerRequest = new OapiCallBackRegisterCallBackRequest();

        registerRequest.setUrl(DingDingConstant.CALLBACK_URL_HOST);
        registerRequest.setAesKey(DingDingConstant.ENCODING_AES_KEY);
        registerRequest.setToken(DingDingConstant.TOKEN);
        registerRequest.setCallBackTag(Collections.singletonList(DingDingConstant.BPMS_INSTANCE_CHANGE));

        OapiCallBackRegisterCallBackResponse registerResponse = client.execute(registerRequest, token);
        logger.info("回调注册结果={}", registerResponse.isSuccess());
    }

    /**
     * 发起钉钉审批
     */
    @PostMapping("/approve")
    private String drop(HttpServletRequest request) {
        String telephone = request.getHeader("telephone");

        try {
            DingTalkClient client = new DefaultDingTalkClient(DingDingConstant.URL_PROCESS_INSTANCE_START);

            OapiProcessinstanceCreateRequest req = new OapiProcessinstanceCreateRequest();
            req.setAgentId(DingDingConstant.AGENT_ID);
            req.setProcessCode(DingDingConstant.PURCHASE_TEMPLATE);
            String dingUserId = this.getByTel(telephone);
            logger.info("根据手机号码获取钉钉userId={}", dingUserId);

            if (StringUtils.isEmpty(dingUserId)) {
                dingUserId = DingDingConstant.DEFAULT_APPLY_BILL_USER_ID;
            }
            logger.info("最终dingUserId:{}", dingUserId);

            req.setOriginatorUserId(dingUserId);

            Long depId = getDepId(dingUserId);
            req.setDeptId(depId);

            List<FormComponentValueVo> componentValues = new ArrayList<>();

            FormComponentValueVo name = new FormComponentValueVo();
            name.setName("公司名称");
            name.setValue("Apple Inc");
            componentValues.add(name);

            FormComponentValueVo tech = new FormComponentValueVo();
            tech.setName("所属行业");
            tech.setValue("high-tech");
            componentValues.add(tech);

            FormComponentValueVo capitalization = new FormComponentValueVo();
            capitalization.setName("今日市值");
            capitalization.setValue("$2 trillion");
            componentValues.add(capitalization);

            FormComponentValueVo procurementPrice = new FormComponentValueVo();
            procurementPrice.setName("预计购入价");
            procurementPrice.setValue("$2.68 trillion");
            componentValues.add(procurementPrice);

            req.setFormComponentValues(componentValues);

            String token = getToken();

            OapiProcessinstanceCreateResponse rsp = client.execute(req, token);
            // 流程实例ID
            String processInstanceId = rsp.getProcessInstanceId();

            logger.info("调用创建审批返回数据：{}", rsp.getBody());
        } catch (ApiException e) {
            logger.error("提交审批异常", e);

            return "提交审批失败";
        }
        return "提交审批成功";
    }

}
