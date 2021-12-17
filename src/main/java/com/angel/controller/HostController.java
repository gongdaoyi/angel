package com.angel.controller;

import com.angel.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("host")
public class HostController {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/redis")
    public String redis(String key) {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries("allbranch:1001");
        String value = (String)redisTemplate.opsForHash().get("allbranch:1001", "level3");
        return value;
    }

    @PostMapping("upload/file")
    public void importExcel(HttpServletRequest request,
                            @RequestParam(value = "file") MultipartFile file,
                            @RequestParam String filePath,
                            @RequestParam String fileName) throws Exception {

        FileUtils.uploadFile(file.getBytes(), filePath, fileName);
    }


}
