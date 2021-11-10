package com.angel.controller;

import com.angel.dict.ActionType;
import com.angel.utils.FileUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("shirley")
public class HostController {

    @PostMapping("enum")
    public void importExcel(@RequestParam(value = "shirley", defaultValue = "0") Double shirley) throws Exception {
        System.out.println(shirley);
    }

    @PostMapping("upload/file")
    public void importExcel(HttpServletRequest request, @RequestParam(value = "file") MultipartFile file,
                            @RequestParam String filePath, @RequestParam String fileName) throws Exception {
        FileUtils.uploadFile(file.getBytes(), filePath, fileName);
    }

    public void test () {
        System.out.println("进来了喔");
    }

}
