//package com.angel.nbop;
//
//import com.alibaba.fastjson.JSONObject;
//import io.swagger.annotations.ApiParam;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Scope;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.io.IOException;
//
//@Scope("prototype")
//@RestController
//@Controller
//@RequestMapping("/arch")
//public class FileServerController {
//
//    @Autowired
//    RelationS3Services relationS3Services;
//
//    @PostMapping("uploadOnlineFile")
//    public JSONObject uploadOnlineFile(
//            @ApiParam(value = "文件服务地址", required = true) @RequestParam("fileUrl") String fileUrl,
//            @ApiParam(value = "日期(yyyyMMdd)") @RequestParam(value = "date", defaultValue = "") String date) throws IOException {
//
//        String downLoadPath = relationS3Services.uploadAWSFile(date, fileUrl);
//
//        JSONObject res = new JSONObject();
//        res.put("code", "200");
//        res.put("msg", "上传成功");
//        res.put("filePath", downLoadPath);
//
//        return res;
//    }
//
//}