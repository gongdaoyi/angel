package com.angel.config;

/**
 * 钉钉审批中的常量定义类
 * @author HP
 */
public interface DingDingConstant {

    /**
     * 钉钉审批模板CODE
     */
    String PURCHASE_TEMPLATE = "PROC-E248DED1-4D1B-4E75-B2C3-0D6AE65ED05B";

    /**
     * 企业corpId, 需要修改成开发者所在企业
     */
    String CORP_ID = "dingebcefefda89f86febc961a6cb783455b";

    /**
     * 应用的agentId，登录开发者后台可查看
     */
    Long AGENT_ID = 929065458L;

    /**
     * 应用的AppKey，登录开发者后台，点击应用管理，进入应用详情可见
     */
    String APP_KEY = "dingwaev23eh43dgsurg";

    /**
     * 应用的AppSecret，登录开发者后台，点击应用管理，进入应用详情可见
     */
    String APP_SECRET = "Z9V9qzW8XuzxnA-IlJHAH2id4t96f-h3h6PywzelbXIiAJQknWfQhn4aaRrDJE3U";

    /**
     * 数据加密密钥。用于回调数据的加密，长度固定为43个字符，从a-z, A-Z, 0-9共62个字符中选取,您可以随机生成
     */
    String ENCODING_AES_KEY = "09p6VZ2JrzVMv19M4rHNdvr5K4tgfkpmHLZgafKXQjJ";

    /**
     * 加解密需要用到的token，企业可以随机填写。如 "12345"
     */
    String TOKEN = "lion";

    /**
     * 默认的申请单审批发起人
     */
    String DEFAULT_APPLY_BILL_USER_ID = "055561325037749375";

    /**
     * 回调host
     */
    String CALLBACK_URL_HOST = "http://gdy.vaiwan.com/process/callback";

    /**
     * 审批实例开始，结束
     */
    String BPMS_INSTANCE_CHANGE = "bpms_instance_change";

    /**
     * 相应钉钉回调时的值
     */
    String CALLBACK_RESPONSE_SUCCESS = "success";

    /**
     * 根据手机号码获取用户id
     */
    String GET_BY_MOBILE = "https://oapi.dingtalk.com/user/get_by_mobile";

    /**
     * 钉钉网关getToken地址
     */
    String URL_GET_TOKEN = "https://oapi.dingtalk.com/gettoken";

    /**
     * 获取用户详情的接口url
     */
    String URL_USER_GET = "https://oapi.dingtalk.com/user/get";

    /**
     * 发起审批实例的接口url
     */
    String URL_PROCESS_INSTANCE_START = "https://oapi.dingtalk.com/topapi/processinstance/create";

    /**
     * 注册企业回调接口url
     */
    String REGISTER_CALLBACK = "https://oapi.dingtalk.com/call_back/register_call_back";

}
