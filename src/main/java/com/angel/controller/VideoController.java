package com.angel.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 视频处理控制器
 *
 * <p>提供视频播放和上传功能</p>
 *
 * @author Angel Development Team
 * @version 1.0.0
 * @since 2025-01-01
 */
@Log4j2
@RestController
@RequestMapping("/video")
public class VideoController {

    @Value("${video.output.dir:./uploads}")
    private String outputDir;

    /**
     * 视频流式播放
     *
     * <p>支持断点续传，适用于大视频文件的流式播放</p>
     *
     * @param request  HTTP请求
     * @param response HTTP响应
     * @param filePath 视频文件路径
     */
    @GetMapping("/play")
    public void play(HttpServletRequest request, HttpServletResponse response,
                     @RequestParam String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"File not found\"}");
            return;
        }

        long fileLength = file.length();
        long rangeStart = 0;
        long rangeEnd = fileLength - 1;

        String rangeString = request.getHeader("Range");
        if (rangeString != null) {
            String rangeValue = rangeString.replace("bytes=", "");
            int dashIndex = rangeValue.indexOf('-');
            if (dashIndex >= 0) {
                String startStr = rangeValue.substring(0, dashIndex);
                String endStr = rangeValue.substring(dashIndex + 1);
                if (!startStr.isEmpty()) {
                    rangeStart = Long.parseLong(startStr);
                }
                if (!endStr.isEmpty()) {
                    rangeEnd = Long.parseLong(endStr);
                }
            }
        }

        long contentLength = rangeEnd - rangeStart + 1;

        response.reset();
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("Content-Range", "bytes " + rangeStart + "-" + rangeEnd + "/" + fileLength);
        response.setHeader("Content-Length", String.valueOf(contentLength));
        response.setContentType("video/mp4");

        if (rangeString != null) {
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        }

        try (RandomAccessFile targetFile = new RandomAccessFile(file, "r")) {
            targetFile.seek(rangeStart);
            byte[] buffer = new byte[8192];
            int bytesRead;
            long remaining = contentLength;
            while (remaining > 0 && (bytesRead = targetFile.read(buffer, 0, (int) Math.min(buffer.length, remaining))) != -1) {
                response.getOutputStream().write(buffer, 0, bytesRead);
                remaining -= bytesRead;
            }
        }
    }

    /**
     * 文件上传
     *
     * @param file 上传的文件
     * @return 上传结果
     */
    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return "File is empty";
        }

        try {
            Path uploadPath = Paths.get(outputDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = file.getOriginalFilename();
            if (fileName == null || fileName.isEmpty()) {
                return "Invalid file name";
            }
            Path filePath = uploadPath.resolve(fileName);

            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }

            Files.copy(file.getInputStream(), filePath);
            log.info("File uploaded successfully: {}", fileName);

            return "File uploaded successfully: " + fileName;

        } catch (IOException e) {
            log.error("File upload failed", e);
            return "File upload failed: " + e.getMessage();
        }
    }
}
