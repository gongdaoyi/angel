//package com.angel.utils;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import sun.misc.BASE64Encoder;
//
//import javax.imageio.ImageIO;
//import java.awt.image.BufferedImage;
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.IOException;
//import java.net.MalformedURLException;
//import java.util.Arrays;
//import java.util.zip.Deflater;
//
/// **
// * 工行转码
// * 图片deflate压缩算法之后再Base64
// * 调用示例 String imageBase64Str = ImageInfoUtil.encodeImgageToBase64(tempFile2);
// *
// * @author : kfzx-libl
// * @since : 2020/4/29 15:58
// */
//public class ImageInfoUtil {
//
//    private static final String FILE_SUFFIX_STR = ".";
//    private static Logger logger = LoggerFactory.getLogger(ImageInfoUtil.class);
//
//    //进行DEFLATE压缩
//    public static byte[] deflateCompress(byte[] input) {
//        logger.info("[deflateCompress]:input length: [{}]", input.length);
//        byte[] output = new byte[1024 * 1024];
//        logger.info("[deflateCompress]output length: [{}]", output.length);
//        Deflater deflater = new Deflater();
//        deflater.setInput(input);
//        deflater.finish();
//        int compressesDateLength = deflater.deflate(output);
//        deflater.end();
//        logger.info("进行DEFLATE压缩 ByteString after deflate is: [{}]", Arrays.copyOf(output, compressesDateLength));
//        return Arrays.copyOf(output, compressesDateLength);
//    }
//
//    public static String encodeImgageToBase64(File imageFile) {
//        // 将图片文件转化为字节数组字符串，并对其进行DEFLATE压缩+Base64编码处理
//        ByteArrayOutputStream outputStream = null;
//        try {
//            BufferedImage bufferedImage = ImageIO.read(imageFile);
//            outputStream = new ByteArrayOutputStream();
//            // 获取文件名后缀
//            String fileSuffixStr = "jpg";
//            if (imageFile.isFile()) {
//                String imageFileName = imageFile.getName();
//                fileSuffixStr = imageFileName.substring(imageFileName.lastIndexOf(FILE_SUFFIX_STR) + 1);
//            }
//
//            ImageIO.write(bufferedImage, fileSuffixStr, outputStream);
//        } catch (MalformedURLException e1) {
//            logger.error("将图片文件转化为字节数组字符串 MalformedURLException e1:", e1);
//        } catch (IOException e) {
//            logger.error("将图片文件转化为字节数组字符串 IOException e:", e);
//        }
//        // 对字节数组Base64编码
//        if (outputStream != null && outputStream.size() > 0) {
//            byte[] b = deflateCompress(outputStream.toByteArray());
//            try {
//                outputStream.close();
//            } catch (IOException e) {
//                logger.error("将图片文件转化为字节数组字符串 关闭outputStream IOException e:", e);
//            }
//            BASE64Encoder encoder = new BASE64Encoder();
//            return encoder.encode(b);// 返回Base64编码过的字节数组字符串
//        }
//
//        return "";// 返回Base64编码过的字节数组字符串
//    }
//}
