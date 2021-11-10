package com.angel.event;

import org.springframework.context.ApplicationEvent;

/**
 * 钉钉审批流事件。
 * <p>
 * 当整个审批流结束时，触发事件通知。
 */
public class ProcessEvent extends ApplicationEvent {

    /**
     * 钉钉审批模版编号
     */
    private String processCode;

    /**
     * 钉钉审批实例ID
     */
    private String processInstanceId;

    /**
     * 钉钉审批结果
     */
    private String result;

    public ProcessEvent(Object source) {
        super(source);
    }

    public String getProcessCode() {
        return processCode;
    }

    public void setProcessCode(String processCode) {
        this.processCode = processCode;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
