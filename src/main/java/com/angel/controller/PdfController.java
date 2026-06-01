package com.angel.controller;

import com.angel.utils.PDFUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.pdfbox.pdmodel.PDDocument;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "application/pdf",
            "application/x-pdf",
            "application/acrobat",
            "apps/interchange",
            "image/vnd.perspective.asap+pdf"
    );

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".pdf", ".PDF");

    private static final int MAX_PAGE_SIZE = 10000;

    private static final long FILE_SIZE_LIMIT = 200 * 1024 * 1024; // 200MB

    private final Set<String> processingTasks = ConcurrentHashMap.newKeySet();

    private final PDFUtils pdfUtils;

    @Value("${ocr.splitPage.tempPath:#{T(java.io.File).createTempFile('temp', '.tmp').getParentFile().absolutePath}}")
    private String tempPath;

    @Autowired
    public PdfController(PDFUtils pdfUtils) {
        this.pdfUtils = pdfUtils;
    }

    /**
     * 合并指定文件夹下的所有PDF文件
     *
     * <p>将指定文件夹中的所有PDF文件合并为一个文件</p>
     *
     * @param folder    文件夹路径
     * @param mergeName 合并后的文件名（不含扩展名）
     * @return 操作结果
     */
    @PostMapping("/merge")
    public ResponseEntity<Map<String, Object>> merge(@RequestParam String folder,
                                                     @RequestParam(defaultValue = "merged") String mergeName) {
        Map<String, Object> response = new HashMap<>();
        long startTime = System.currentTimeMillis();

        try {
            // 参数校验
            File folderFile = validateAndGetFolder(folder);
            List<File> pdfFiles = collectPdfFiles(folderFile);

            if (pdfFiles.isEmpty()) {
                return buildErrorResponse(response, "No PDF files found in the specified folder", 400);
            }

            // 生成输出文件路径
            String outputFileName = mergeName.endsWith(".pdf") ? mergeName : mergeName + ".pdf";
            String mergeFilePath = folderFile.getAbsolutePath() + File.separator + outputFileName;
            File outputFile = new File(mergeFilePath);

            // 检查输出文件是否已存在
            if (outputFile.exists()) {
                return buildErrorResponse(response, "Output file already exists: " + outputFileName, 400);
            }

            // 生成任务ID并记录
            String taskId = generateTaskId();
            processingTasks.add(taskId);

            try {
                log.info("Starting PDF merge - TaskId: {}, Folder: {}, FileCount: {}",
                        taskId, folder, pdfFiles.size());

                // 执行合并
                pdfUtils.combine(folderFile, mergeFilePath, null);

                // 获取合并后的页数
                int totalPages;
                try (PDDocument doc = PDDocument.load(outputFile)) {
                    totalPages = doc.getNumberOfPages();
                }

                long duration = System.currentTimeMillis() - startTime;

                response.put("success", true);
                response.put("taskId", taskId);
                response.put("message", "PDF merge completed successfully");
                response.put("outputPath", mergeFilePath);
                response.put("outputFileName", outputFileName);
                response.put("mergedFileCount", pdfFiles.size());
                response.put("totalPages", totalPages);
                response.put("durationMs", duration);

                log.info("PDF merge completed - TaskId: {}, Duration: {}ms", taskId, duration);
                return ResponseEntity.ok(response);

            } finally {
                processingTasks.remove(taskId);
            }

        } catch (SecurityException e) {
            log.error("Access denied for folder: {}", folder, e);
            return buildErrorResponse(response, "Access denied: " + folder, 403);

        } catch (Exception e) {
            log.error("PDF merge failed for folder: {}", folder, e);
            return buildErrorResponse(response, "PDF merge failed: " + e.getMessage(), 500);
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
    @PostMapping("/split")
    public ResponseEntity<Map<String, Object>> split(@RequestParam(value = "file") MultipartFile file,
                                                     @RequestParam(defaultValue = "20") int pageSize) {
        Map<String, Object> response = new HashMap<>();
        long startTime = System.currentTimeMillis();

        try {
            // 文件非空校验
            if (file.isEmpty()) {
                return buildErrorResponse(response, "File is empty", 400);
            }

            // 文件大小校验
            if (file.getSize() > FILE_SIZE_LIMIT) {
                return buildErrorResponse(response,
                        "File size exceeds limit of " + (FILE_SIZE_LIMIT / 1024 / 1024) + "MB", 400);
            }

            // 内容类型校验
            String contentType = file.getContentType();
            String originalFilename = file.getOriginalFilename();
            if (!isValidPdfFile(contentType, originalFilename)) {
                return buildErrorResponse(response,
                        "Invalid file type. Only PDF files are allowed", 400);
            }

            // 页数参数校验
            if (pageSize <= 0) {
                return buildErrorResponse(response, "Page size must be greater than 0", 400);
            }
            if (pageSize > MAX_PAGE_SIZE) {
                return buildErrorResponse(response,
                        "Page size exceeds maximum limit of " + MAX_PAGE_SIZE, 400);
            }

            // 临时目录校验
            File tempDir = ensureTempDirectory();
            if (tempDir == null) {
                return buildErrorResponse(response, "Failed to create/access temp directory", 500);
            }

            // 生成任务ID并记录
            String taskId = generateTaskId();
            processingTasks.add(taskId);

            try {
                log.info("Starting PDF split - TaskId: {}, FileName: {}, PageSize: {}",
                        taskId, originalFilename, pageSize);

                // 验证PDF文件是否有效
                int totalPages;
                try (PDDocument tempDoc = PDDocument.load(file.getInputStream())) {
                    totalPages = tempDoc.getNumberOfPages();
                }

                if (totalPages == 0) {
                    return buildErrorResponse(response, "PDF file contains no pages", 400);
                }

                // 执行拆分
                List<String> splitFileNames = pdfUtils.pageSpilt(
                        file.getInputStream(), tempDir, pageSize);

                // 构建输出文件路径列表
                List<Map<String, Object>> splitFiles = splitFileNames.stream()
                        .map(name -> {
                            Map<String, Object> fileInfo = new HashMap<>();
                            fileInfo.put("fileName", name);
                            fileInfo.put("path", tempDir.getAbsolutePath() + File.separator + name);
                            fileInfo.put("downloadUrl", "/pdf/download?fileName=" + name);
                            return fileInfo;
                        })
                        .collect(Collectors.toList());

                long duration = System.currentTimeMillis() - startTime;
                int splitCount = splitFileNames.size();

                response.put("success", true);
                response.put("taskId", taskId);
                response.put("message", "PDF split completed successfully");
                response.put("originalFileName", originalFilename);
                response.put("originalTotalPages", totalPages);
                response.put("pageSize", pageSize);
                response.put("splitFileCount", splitCount);
                response.put("splitFiles", splitFiles);
                response.put("tempDirectory", tempDir.getAbsolutePath());
                response.put("durationMs", duration);

                log.info("PDF split completed - TaskId: {}, SplitCount: {}, Duration: {}ms",
                        taskId, splitCount, duration);
                return ResponseEntity.ok(response);

            } finally {
                processingTasks.remove(taskId);
            }

        } catch (IOException e) {
            log.error("PDF split failed - IO error", e);
            return buildErrorResponse(response, "Failed to read file: " + e.getMessage(), 500);

        } catch (Exception e) {
            log.error("Unexpected error during PDF split", e);
            return buildErrorResponse(response, "Unexpected error: " + e.getMessage(), 500);
        }
    }

    /**
     * 校验并获取文件夹
     */
    private File validateAndGetFolder(String folder) {
        if (folder == null || folder.trim().isEmpty()) {
            throw new IllegalArgumentException("Folder path cannot be empty");
        }

        File folderFile = new File(folder);

        if (!folderFile.exists()) {
            throw new IllegalArgumentException("Folder does not exist: " + folder);
        }

        if (!folderFile.isDirectory()) {
            throw new IllegalArgumentException("Path is not a directory: " + folder);
        }

        if (!folderFile.canRead()) {
            throw new SecurityException("No read permission for folder: " + folder);
        }

        return folderFile;
    }

    /**
     * 收集文件夹中的PDF文件
     */
    private List<File> collectPdfFiles(File folder) {
        File[] files = folder.listFiles((dir, name) ->
                name.toLowerCase().endsWith(".pdf"));

        if (files == null || files.length == 0) {
            return Collections.emptyList();
        }

        return Arrays.stream(files)
                .filter(File::isFile)
                .filter(File::canRead)
                .sorted(Comparator.comparing(File::getName))
                .collect(Collectors.toList());
    }

    /**
     * 验证PDF文件
     */
    private boolean isValidPdfFile(String contentType, String fileName) {
        // 检查内容类型
        if (contentType != null && ALLOWED_CONTENT_TYPES.contains(contentType)) {
            return true;
        }

        // 检查文件扩展名
        if (fileName != null) {
            return ALLOWED_EXTENSIONS.stream()
                    .anyMatch(ext -> fileName.toLowerCase().endsWith(ext));
        }

        return false;
    }

    /**
     * 确保临时目录存在且可写
     */
    private File ensureTempDirectory() {
        if (tempPath == null || tempPath.trim().isEmpty()) {
            tempPath = System.getProperty("java.io.tmpdir");
        }

        Path tempDirPath = Paths.get(tempPath);

        try {
            if (!Files.exists(tempDirPath)) {
                Files.createDirectories(tempDirPath);
            }

            if (!Files.isDirectory(tempDirPath)) {
                log.error("Temp path exists but is not a directory: {}", tempPath);
                return null;
            }

            if (!Files.isWritable(tempDirPath)) {
                log.error("Temp directory is not writable: {}", tempPath);
                return null;
            }

            return tempDirPath.toFile();

        } catch (IOException e) {
            log.error("Failed to create/access temp directory: {}", tempPath, e);
            return null;
        }
    }

    /**
     * 生成任务ID
     */
    private String generateTaskId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * 构建错误响应
     */
    private ResponseEntity<Map<String, Object>> buildErrorResponse(
            Map<String, Object> response, String message, int status) {
        response.put("success", false);
        response.put("message", message);
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.status(status).body(response);
    }

}
