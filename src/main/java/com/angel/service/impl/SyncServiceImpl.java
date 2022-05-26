package com.angel.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.angel.mapper.CrdtBlackListMapper;
import com.angel.sync.AsynExecutor;
import com.angel.sync.AsynExecutorResult;
import com.angel.sync.ExecutorStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SyncServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(SyncServiceImpl.class);

    @Autowired
    private CrdtBlackListMapper crdtBlackListMapper;

    /**
     * 推送企业微信消息
     *
     * @author: GonGDaoYi
     * @date: 2022/05/23
     * @jira: BOPS-21714
     */
    public void sendWeComMessage() {
        AsynExecutor executor = new AsynExecutor();
        Map<String, Integer> keyIndex = new HashMap<>();

        List<JSONObject> list = crdtBlackListMapper.listCrdtBlackList("clientId", "str");

        for (JSONObject data : list) {
            int index = executor.submitIndex(() -> this.callWeComSync(data));
            keyIndex.put(data.getString("id"), index);
        }

        // 可以不使用这个返回值，但是executor.getExecutorFinalStatus()必须使用，这是等待所有任务完成的
        ExecutorStatus executorFinalStatus = executor.getExecutorFinalStatus();
        if (executorFinalStatus == ExecutorStatus.FAIL) {
            // 表示有一个任务执行失败了，看看是抛异常还是怎么处理
            // 这里的失败不是程序的异常，而是任务失败
        }

        ArrayList<AsynExecutorResult> syncResult = executor.getAllResultList();

        for (String key : keyIndex.keySet()) {
            Integer index = keyIndex.get(key);
            AsynExecutorResult<String> subResult = syncResult.get(index);

            if (subResult.getStatus() == ExecutorStatus.SUCESS) {
                String resData = subResult.getResult();

                if (!StringUtils.isEmpty(resData) && "200".equals(resData)) {
                    // 任务成功（双重判断1 任务状态 2 自己定义的返回值）
                } else {
                    // 失败
                    logger.error("异步第[{}]笔[{}]执行成功，结果异常: {}", index, key, resData);
                }
            } else if (subResult.getStatus() == ExecutorStatus.FAIL) {
                Throwable exception = subResult.getException();
                // 失败
                logger.error("异步第[{}]笔[{}]执行失败: {}", index, key, exception.getMessage());
            } else {
                // 失败
                logger.error("异步第[{}]笔[{}]执行异常，状态为: {}", index, key, subResult.getStatus().getValue());
            }
        }
    }

    private String callWeComSync(JSONObject data) {
        // 需要并发执行的任务

        return "200";
    }

}
