package com.angel.controller;

import com.alibaba.fastjson.JSONObject;
import com.angel.entity.TaxIdInfoCheck;
import com.angel.entity.WhiteList;
import com.angel.model.User;
import com.angel.service.ICrdtBlackListService;
import com.angel.service.ITaxIdInfoCheckService;
import com.angel.service.IWhiteListService;
import com.angel.service.TestService;
import com.angel.service.impl.SyncServiceImpl;
import com.angel.sync.AsynExecutor;
import com.angel.sync.AsynExecutorResult;
import com.angel.sync.ExecutorStatus;
import com.angel.utils.FileUtils;
import com.angel.utils.PDFUtils;
import com.angel.utils.RedisUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/host")
public class HostController {

    private static final Logger log = LoggerFactory.getLogger(HostController.class);

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    IWhiteListService whiteListService;

    @Autowired
    ITaxIdInfoCheckService taxIdInfoCheckService;
    @Autowired
    ICrdtBlackListService crdtBlackListService;

    @Autowired
    TestService testService;

    @Value("#{'${list.type}'.split(',')}")
    private List<String> listType;

    @Value("${splitPage.ocr.tempPath}")
    private String tempPath;

    @Autowired
    private SyncServiceImpl syncService;

    @Autowired
    private PDFUtils pdfUtils;

    /**
     * OCR识别
     */
    @PostMapping("/ocr")
    public List<String> ocr(@RequestParam(value = "file") MultipartFile file,
                            int batchNumber) throws IOException {
        List<String> result = new ArrayList<>();

        List<String> tempFileNameList = pdfUtils.pageSpilt(file.getInputStream(), new File(tempPath), batchNumber);

        try {
            AsynExecutor asynExecutor = new AsynExecutor();

            List<Integer> indexList = new ArrayList<>();
            for (String tempFileName : tempFileNameList) {
                indexList.add(asynExecutor.submitIndex(() -> this.syncOrc(tempFileName)));
            }

            asynExecutor.getExecutorFinalStatus();

            ArrayList<AsynExecutorResult> allResultList = asynExecutor.getAllResultList();
            for (Integer index : indexList) {
                AsynExecutorResult<String> asynExecutorResult = allResultList.get(index);
                if (ExecutorStatus.SUCESS == asynExecutorResult.getStatus()) {
                    result.add(asynExecutorResult.getResult());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("OCR识别异常");
        } finally {
            // 资源闭环, 如果发生异常。 服务器会被大量临时文件堆积爆满
            for (String tempFileName : tempFileNameList) {
                File tempFile = new File(tempPath + tempFileName);
                tempFile.delete();
            }
        }

        return result;
    }

    private String syncOrc(String tempFile) throws InterruptedException {
        Thread.sleep(3000);

        return tempFile;
    }


    @PostMapping("/send")
    public String sendRabbitMQ() {

        syncService.sendRabbitMQ();

        return "发送成功";
    }

    @PostMapping("/update")
    public int update(@RequestBody User user) {

        return crdtBlackListService.update(user);
    }

    @GetMapping("qryMysql")
    public List<JSONObject> listCrdtBlackList(@RequestParam String clientId) {
        return crdtBlackListService.listCrdtBlackList(clientId);
    }

    @GetMapping("/redis")
    public String redis(String key) {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries("allbranch:1001");
        return (String) redisTemplate.opsForHash().get("allbranch:1001", "level3");
    }

    @PostMapping("upload/file")
    public void importExcel(HttpServletRequest request, @RequestParam(value = "file") MultipartFile file, @RequestParam String filePath, @RequestParam String fileName) throws Exception {

        FileUtils.uploadFile(file.getBytes(), filePath, fileName);
    }

    @PostMapping("rebbit")
    public String sendQueen(String msg) {
        String messageId = String.valueOf(UUID.randomUUID());
        String messageData = "test message, hello!";
        String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Map<String, Object> map = new HashMap<>();
        map.put("messageId", messageId);
        map.put("message", msg);
        map.put("messageData", messageData);
        map.put("createTime", createTime);

        //将消息携带绑定键值：TestDirectRouting 发送到交换机TestDirectExchange
        rabbitTemplate.convertAndSend("TestDirectExchange", "TestDirectRouting", map);

        return "send succeed";
    }

    @GetMapping("query")
    public List<WhiteList> query(@RequestParam String name) {
        return whiteListService.list(Wrappers.<WhiteList>lambdaQuery().eq(WhiteList::getName, name));
    }


    @GetMapping("listTaxIdInfoCheck")
    public Object queryTest(@RequestParam String name) {
        TaxIdInfoCheck taxIdInfoCheck = taxIdInfoCheckService.getOne(Wrappers.<TaxIdInfoCheck>lambdaQuery().eq(TaxIdInfoCheck::getNationalityName, name));
        if (ObjectUtils.isEmpty(taxIdInfoCheck)) {
            return "没有数据喔" + taxIdInfoCheck.getEnCheckBits();
        }
        return taxIdInfoCheck;
    }


    @PostMapping("updateTaxIdInfoCheck")
    public boolean updateTaxIdInfoCheck(@RequestParam String nationalityName) {
        return taxIdInfoCheckService.updateTaxIdInfoCheck(nationalityName);
    }

}
