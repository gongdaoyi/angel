package com.angel.controller;

import com.alibaba.fastjson.JSONObject;
import com.angel.service.impl.MatcherServiceImpl;
import com.angel.utils.PDFUtils;
import com.angel.utils.sync.AsynExecutor;
import com.angel.utils.sync.AsynExecutorResult;
import com.angel.utils.sync.ExecutorStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/ocr")
public class OcrController {

    @Autowired
    private PDFUtils pdfUtils;


    @Value("${ocr.splitPage.tempPath}")
    private String tempPath;

    @Autowired
    private MatcherServiceImpl matcherService;

    @GetMapping("/archOcr")
    public JSONObject archOcr() throws IOException {

        return matcherService.archOcr();
    }

    /**
     * 多线程-OCR识别
     */
    @PostMapping("/ocr")
    public List<String> ocr(@RequestParam(value = "file") MultipartFile file,
                            @RequestParam int parallelMaxPage) throws IOException {
        List<String> result = new ArrayList<>();

        List<String> tempFileNameList = pdfUtils.pageSpilt(file.getInputStream(), new File(tempPath), parallelMaxPage);

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
            for (String tempFileName : tempFileNameList) {
                File tempFile = new File(tempPath + tempFileName);
                tempFile.delete();
            }
        }

        return result;
    }

    private String syncOrc(String tempFile) throws InterruptedException {
        Thread.sleep(3000);
        // 异步OCR识别具体操作

        return tempFile;
    }
}
