package com.angel.event;

import com.angel.config.DingDingConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author HP
 */

@Component
public class ProcessEventListener {

    private static final Logger log = LoggerFactory.getLogger(ProcessEventListener.class);

    private static final String DING_DING_AGREE = "agree";

    @EventListener(condition = "#processEvent.processCode == '" + DingDingConstant.PURCHASE_TEMPLATE + "'")
    public void teacherAllot(ProcessEvent processEvent) {
        String result = processEvent.getResult();
        String status = DING_DING_AGREE.equals(result) ? "通过" : "拒绝";
        log.info("钉钉审批状态：{}", status);
    }

}
