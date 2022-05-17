package com.angel.controller;

import com.angel.entity.TaxIdInfoCheck;
import com.angel.entity.WhiteList;
import com.angel.service.ICrdtBlackListService;
import com.angel.service.ITaxIdInfoCheckService;
import com.angel.service.IWhiteListService;
import com.angel.utils.FileUtils;
import com.angel.utils.RedisUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("host")
public class HostController {

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
    @Value("#{'${list.type}'.split(',')}")
    private List<String> listType;

    @GetMapping("listCrdtBlackList")
    public Object listCrdtBlackList(@RequestParam String clientId, @RequestParam String str) {

        return crdtBlackListService.listCrdtBlackList(clientId, str);
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
