package com.angel.controller;

import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/video")
@Log4j2
public class VideoController {

    private static final Logger logger = LoggerFactory.getLogger(VideoController.class);

    private static final String errorFileDir = "C:\\Users\\angle\\working\\NBOP\\out\\";

    @GetMapping("/play2")
    public void orgModel(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String filePath = "C:\\Users\\angle\\working\\NBOP\\out\\2023~~abdc.mp4";
        //视频资源存储信息
        response.reset();
        //获取从那个字节开始读取文件
        String rangeString = request.getHeader("Range");
        if (rangeString == null) {
            rangeString = "bytes=0-";
        }

        File file = null;
        OutputStream outputStream = null;
        RandomAccessFile targetFile = null;
        try {
            //获取响应的输出流
            outputStream = response.getOutputStream();
            //读取本地视频
            file = new File(filePath);
            targetFile = new RandomAccessFile(file, "r");
            if (file.exists()) {
                long fileLength = targetFile.length();
                long range = Long.valueOf(rangeString.substring(rangeString.indexOf("=") + 1, rangeString.indexOf("-")));
                //设置内容类型
                response.setHeader("Content-Type", "video/mp4");

                //设置此次相应返回的数据长度
                String contentLength = String.valueOf(fileLength - range);
                response.setHeader("Content-Length", contentLength);

                //设置此次相应返回的数据范围
                String contentRange = "bytes " + range + "-" + (fileLength - 1) + "/" + fileLength;
                response.setHeader("Content-Range", contentRange);

                //返回码需要为206，而不是200
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                //设定文件读取开始位置（以字节为单位）
                targetFile.seek(range);

                byte[] cache = new byte[1024 * 300];
                int flag;
                while ((flag = targetFile.read(cache)) != -1) {
                    outputStream.write(cache, 0, flag);
                }
            } else {
                String message = "file: not exists";
                //解决编码问题
                response.setHeader("Content-Type", "application/json");
                outputStream.write(message.getBytes(StandardCharsets.UTF_8));
            }

        } catch (Exception e) {
            throw new RuntimeException("视频播放失败", e);
        } finally {
            outputStream.flush();
            outputStream.close();
            targetFile.close();

            boolean delete = file.delete();
            //log.info("删除源文件{}", delete);
        }
    }

    @GetMapping("play")
    public void play(HttpServletResponse response) throws IOException {
        File file = new File("C:\\Users\\angle\\working\\NBOP\\out\\2023~~abdc.mp4");

        String name = file.getName();

        InputStream in = new FileInputStream(file);
        // 创建字节数组，数组大小为视频文件大小
        byte[] data = new byte[in.available()];
        in.read(data);
        response.setContentType("video/mp4");
        //response.addHeader("Content-Disposition", "attachment;fileName=2023~~abdc.mp4");
        response.setContentLength(data.length);
        response.setHeader("Content-Range", "" + (data.length - 1));
        response.setHeader("Accept-Ranges", "bytes");
        OutputStream os = response.getOutputStream();
        // 将视频文件的字节数组写入 response 中
        os.write(data);
        os.flush();
        os.close();
        in.close();
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return "File is empty";
        }

        try {
            // 这里假设你想要覆盖的文件位于同一个目录下
            String uploadDir = "./"; // 或者其他你想要上传文件的目录
            String fileName = file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir).resolve(fileName);

            // 删除已存在的文件
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }

            // 保存文件
            Files.copy(file.getInputStream(), filePath);

            return "File uploaded successfully: " + fileName;

        } catch (IOException e) {
            e.printStackTrace();
            return "File upload failed: " + e.getMessage();
        }
    }
}
