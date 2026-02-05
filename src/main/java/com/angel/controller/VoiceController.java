package com.angel.controller;

import com.angel.service.VoiceService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 语音识别控制器
 *
 * <p>提供语音文件转文字功能</p>
 *
 * @author Angel Development Team
 * @version 1.0.0
 * @since 2025-01-01
 */
@Log4j2
@RestController
@RequestMapping("/api/voice")
public class VoiceController {

    private final VoiceService voiceService;

    /**
     * 构造函数
     *
     * @param voiceService 语音服务
     */
    @Autowired
    public VoiceController(VoiceService voiceService) {
        this.voiceService = voiceService;
    }

    /**
     * 语音文件转文字
     *
     * @param file 语音文件
     * @return 转换后的文字结果
     */
    @PostMapping("/convert")
    public ResponseEntity<String> convertVoiceToText(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty");
            }

            String result = voiceService.voiceToText(file);
            log.info("Voice to text conversion completed successfully");
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("Voice to text conversion failed", e);
            return ResponseEntity.status(500).body("Conversion failed: " + e.getMessage());
        }
    }

}
