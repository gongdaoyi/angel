package com.angel.controller;

import com.angel.utils.PDFUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * PDF处理控制器
 *
 * <p>提供PDF文件的合并、拆分功能</p>
 *
 * @author Angel Development Team
 * @version 1.0.0
 * @since 2025-01-01
 */
@Log4j2
@RestController
@RequestMapping("/pdf")
public class PdfController {

    /**
     * PDF临时文件存储路径（从配置文件注入）
     */
    @Value("${ocr.splitPage.tempPath}")
    private String tempPath;

    /**
     * PDF工具类
     */
    @Autowired
    private PDFUtils pdfUtils;

    /**
     * 合并指定文件夹下的所有PDF文件
     *
     * <p>将指定文件夹中的所有PDF文件合并为一个文件</p>
     *
     * @param folder    文件夹路径
     * @param mergeName 合并后的文件名
     * @return 操作结果
     */
    @PostMapping("/merge")
    public ResponseEntity<String> merge(@RequestParam String folder,
                                        @RequestParam String mergeName) {
        try {
            File file = new File(folder);
            String mergeFilePath = folder + File.separator + mergeName;

            pdfUtils.combine(file, mergeFilePath, null);

            log.info("PDF files merged successfully to: {}", mergeFilePath);
            return ResponseEntity.ok("PDF merge completed: " + mergeFilePath);

        } catch (Exception e) {
            log.error("PDF merge failed for folder: {}", folder, e);
            return ResponseEntity.status(500)
                    .body("PDF merge failed: " + e.getMessage());
        }
    }

    /**
     * 拆分PDF文件
     *
     * <p>将上传的PDF文件按照指定页数拆分为多个子PDF文件</p>
     *
     * @param file     上传的PDF文件
     * @param pageSize 每个子PDF的页数
     * @return 操作结果
     */
    @PostMapping("/spilt")
    public ResponseEntity<String> spilt(@RequestParam(value = "file") MultipartFile file,
                                        @RequestParam int pageSize) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty");
            }

            if (pageSize <= 0) {
                return ResponseEntity.badRequest().body("Page size must be greater than 0");
            }

            pdfUtils.pageSpilt(file.getInputStream(), new File(tempPath), pageSize);

            log.info("PDF split completed with page size: {}", pageSize);
            return ResponseEntity.ok("PDF split completed successfully");

        } catch (IOException e) {
            log.error("PDF split failed", e);
            return ResponseEntity.status(500)
                    .body("PDF split failed: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during PDF split", e);
            return ResponseEntity.status(500)
                    .body("Unexpected error: " + e.getMessage());
        }
    }
}
