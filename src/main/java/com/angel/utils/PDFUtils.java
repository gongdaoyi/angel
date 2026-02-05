package com.angel.utils;

import lombok.extern.log4j.Log4j2;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * PDF工具类
 *
 * <p>提供PDF文件的加载、拆分、合并、图片转PDF等功能</p>
 * <p>
 * 功能列表：
 * <ul>
 *   <li>从文件或流加载PDF</li>
 *   <li>创建空白PDF文档</li>
 *   <li>获取PDF页数和页面列表</li>
 *   <li>PDF拆分（按指定页数）</li>
 *   <li>PDF合并（支持自定义排序）</li>
 *   <li>图片转PDF</li>
 *   <li>获取PDF页面分辨率</li>
 * </ul>
 *
 * @author Angel Development Team
 * @version 1.0.0
 * @since 2025-01-01
 */
@Component
@Log4j2
public class PDFUtils {

    /**
     * PDF文件扩展名
     */
    private static final String PDF_EXTENSION = ".pdf";

    /**
     * 列表初始容量
     */
    private static final int INITIAL_CAPACITY = 64;

    /**
     * 文件名分隔符
     */
    private static final String FILE_NAME_SEPARATOR = ".";

    /**
     * 从文件中加载PDF文档
     *
     * @param file PDF文件对象
     * @return PDF文档对象，如果文件不存在或为目录则返回null
     * @throws IOException 读取文件时的IO异常
     */
    public PDDocument load(File file) throws IOException {
        if (!file.exists() || file.isDirectory()) {
            log.warn("PDF file does not exist or is a directory: {}", file.getAbsolutePath());
            return null;
        }

        return PDDocument.load(file);
    }

    /**
     * 从输入流中加载PDF文档
     *
     * @param inputStream PDF文件输入流
     * @return PDF文档对象，如果流为空或无数据则返回null
     * @throws IOException 读取流时的IO异常
     */
    public PDDocument load(InputStream inputStream) throws IOException {
        if (inputStream == null || inputStream.available() == 0) {
            log.warn("Input stream is null or empty");
            return null;
        }

        return PDDocument.load(inputStream);
    }

    /**
     * 创建一个单页的空白PDF文档
     *
     * @param outputFile 输出文件路径
     * @return 创建的PDF文档对象
     * @throws IOException 保存文件时的IO异常
     */
    public PDDocument getBlankPDF(File outputFile) throws IOException {
        log.debug("Creating blank PDF at: {}", outputFile.getAbsolutePath());

        PDDocument pdf = new PDDocument();
        PDPage blankPage = new PDPage();
        pdf.addPage(blankPage);
        pdf.save(outputFile);

        log.info("Blank PDF created successfully: {}", outputFile.getAbsolutePath());
        return pdf;
    }

    /**
     * 获取PDF文档的总页数
     *
     * @param pdf PDF文档对象
     * @return 总页数
     */
    public int pageCount(PDDocument pdf) {
        return pdf.getNumberOfPages();
    }

    /**
     * 获取PDF文档的所有页面对象
     *
     * @param pdf PDF文档对象
     * @return 页面对象列表
     */
    public List<PDPage> getPageList(PDDocument pdf) {
        int count = pageCount(pdf);
        List<PDPage> pages = new ArrayList<>(INITIAL_CAPACITY);
        PDPageTree pdPages = pdf.getPages();

        for (int i = 0; i < count; i++) {
            PDPage pdPage = pdPages.get(i);
            pages.add(pdPage);
        }

        return pages;
    }

    /**
     * 拆分PDF文件
     *
     * <p>将PDF文件按照指定页数拆分为多个子PDF文件</p>
     *
     * @param inputStream     PDF文件输入流
     * @param outputParent    输出文件的父目录
     * @param parallelMaxPage 拆分子文件最大页数（例如：每20页拆分为一个PDF文件）
     * @return 生成的临时文件名列表
     * @throws IOException      读写文件时的IO异常
     * @throws RuntimeException 输出目录不存在或处理失败时抛出
     */
    public List<String> pageSpilt(InputStream inputStream, File outputParent, int parallelMaxPage)
            throws IOException {
        // 验证输出目录
        if (!outputParent.exists() || !outputParent.isDirectory()) {
            String errorMsg = "输出文件的父目录不存在: " + outputParent.getAbsolutePath();
            log.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }

        if (parallelMaxPage <= 0) {
            String errorMsg = "拆分页数必须大于0，当前值: " + parallelMaxPage;
            log.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }

        PDDocument tempPdf = new PDDocument();
        List<File> tempFiles = new ArrayList<>();
        List<String> tempFileNames = new ArrayList<>();

        try (PDDocument sourcePdf = load(inputStream)) {
            int currentPage = 1;
            int totalPages = pageCount(sourcePdf);

            log.info("开始拆分PDF，总页数: {}, 每个子文件最大页数: {}", totalPages, parallelMaxPage);

            while (currentPage <= totalPages) {
                PDPage page = sourcePdf.getPage(currentPage - 1);
                tempPdf.addPage(page);

                // 达到指定页数或最后一页时，保存临时文件
                if (currentPage % parallelMaxPage == 0 || currentPage == totalPages) {
                    String tempFileName = currentPage + PDF_EXTENSION;
                    File file = new File(outputParent, tempFileName);
                    tempPdf.save(file);
                    close(tempPdf);

                    tempPdf = new PDDocument();
                    tempFiles.add(file);
                    tempFileNames.add(tempFileName);

                    log.debug("已生成临时文件: {}/{}", currentPage, totalPages);
                }
                currentPage++;
            }

            log.info("PDF拆分完成 - 原文件{}页, 按每个最大{}页, 已拆为{}个临时文件",
                    totalPages, parallelMaxPage, tempFileNames.size());

        } catch (Exception e) {
            log.error("PDF拆分失败，开始清理临时文件", e);

            // 清理已生成的临时文件
            for (File tempFile : tempFiles) {
                if (tempFile.exists() && !tempFile.delete()) {
                    log.warn("无法删除临时文件: {}", tempFile.getAbsolutePath());
                }
            }

            throw new RuntimeException("PDF拆分失败: " + e.getMessage(), e);
        } finally {
            close(tempPdf);
            close(inputStream);
        }

        return tempFileNames;
    }

    /**
     * 合并指定文件夹下的所有PDF文件
     *
     * @param inputParent 输入文件夹（包含待合并的PDF文件）
     * @param outputFile  输出文件路径
     * @param sortor      文件排序器（可为null）
     * @throws IOException      读写文件时的IO异常
     * @throws RuntimeException 输入目录不存在或输出文件已存在时抛出
     */
    public void combine(File inputParent, String outputFile, FileSortor sortor) throws IOException {
        // 验证输入目录
        if (!inputParent.exists() || !inputParent.isDirectory()) {
            String errorMsg = "输入文件的父目录不存在: " + inputParent.getAbsolutePath();
            log.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }

        // 验证输出文件
        File outputFileObj = new File(outputFile);
        if (outputFileObj.exists()) {
            String errorMsg = "输出文件已存在: " + outputFile;
            log.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }

        log.info("开始合并PDF文件，源目录: {}, 目标文件: {}", inputParent.getAbsolutePath(), outputFile);

        File[] files = inputParent.listFiles();
        if (files == null || files.length == 0) {
            log.warn("源目录中没有找到文件");
            return;
        }

        // 文件排序
        if (sortor != null) {
            sortor.sort(files);
        }

        PDFMergerUtility merger = new PDFMergerUtility();
        merger.setDestinationFileName(outputFile);

        int fileCount = 0;
        for (File file : files) {
            if (file.getName().toLowerCase().endsWith(PDF_EXTENSION)) {
                merger.addSource(file);
                fileCount++;
            }
        }

        if (fileCount == 0) {
            log.warn("未找到PDF文件进行合并");
            return;
        }

        merger.mergeDocuments(null);
        log.info("PDF合并完成，共合并{}个文件", fileCount);
    }

    /**
     * 获取PDF页面的分辨率
     *
     * @param page PDF页面对象
     * @return 分辨率字符串，格式为"宽度*高度"
     */
    public String getResolution(PDPage page) {
        PDRectangle rectangle = page.getArtBox();
        double width = Math.ceil(rectangle.getWidth());
        double height = Math.ceil(rectangle.getHeight());
        return (int) width + "*" + (int) height;
    }

    /**
     * 将图片转换为PDF
     *
     * @param inputFile  图片文件路径
     * @param outputFile 输出PDF文件路径
     * @throws IOException      读写文件时的IO异常
     * @throws RuntimeException 输入文件不存在或输出文件不是PDF格式时抛出
     */
    public void convertImgToPDF(String inputFile, String outputFile) throws IOException {
        // 验证输入文件
        File inputFileObj = new File(inputFile);
        if (!inputFileObj.exists()) {
            String errorMsg = "输入文件不存在: " + inputFile;
            log.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }

        // 验证输出文件格式
        if (!outputFile.toLowerCase().endsWith(PDF_EXTENSION)) {
            String errorMsg = "只能转成PDF文件，当前输出文件: " + outputFile;
            log.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }

        log.info("开始转换图片到PDF，源文件: {}, 目标文件: {}", inputFile, outputFile);

        PDDocument document = new PDDocument();
        InputStream inputStream = new FileInputStream(inputFile);

        try {
            BufferedImage bimg = ImageIO.read(inputStream);
            float width = bimg.getWidth();
            float height = bimg.getHeight();

            PDPage page = new PDPage(new PDRectangle(width, height));
            document.addPage(page);

            PDImageXObject img = PDImageXObject.createFromFile(inputFile, document);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.drawImage(img, 0, 0, width, height);
            contentStream.close();

            document.save(outputFile);
            log.info("图片转PDF完成，分辨率: {}*{}", width, height);

        } finally {
            close(inputStream);
            close(document);
        }
    }

    /**
     * 安全关闭输入流
     *
     * @param inputStream 输入流对象
     */
    public void close(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                log.warn("关闭输入流时发生异常", e);
            }
        }
    }

    /**
     * 安全关闭PDF文档
     *
     * @param pdf PDF文档对象
     */
    public void close(PDDocument pdf) {
        if (pdf != null) {
            try {
                pdf.close();
            } catch (IOException e) {
                log.warn("关闭PDF文档时发生异常", e);
            }
        }
    }

    /**
     * 文件排序器接口
     *
     * <p>用于在合并PDF时对文件进行自定义排序</p>
     */
    public interface FileSortor {
        /**
         * 对源文件组进行排序
         *
         * @param sources 源文件数组
         */
        void sort(File[] sources);
    }

}
