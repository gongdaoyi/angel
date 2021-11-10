//package com.angel.nbop;
//
//import com.amazonaws.*;
//import com.amazonaws.auth.AWSCredentials;
//import com.amazonaws.auth.BasicAWSCredentials;
//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.AmazonS3Client;
//import com.amazonaws.services.s3.S3ClientOptions;
//import com.amazonaws.services.s3.model.GetObjectRequest;
//import com.amazonaws.services.s3.model.ObjectMetadata;
//import com.amazonaws.services.s3.model.PutObjectRequest;
//import com.amazonaws.services.s3.model.S3Object;
//import com.amazonaws.services.s3.transfer.TransferManager;
//import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
//import com.amazonaws.services.s3.transfer.Upload;
//import com.amazonaws.util.IOUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.util.StringUtils;
//
//import java.io.*;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.Calendar;
//
//@Service
//public class RelationS3Services {
//
//    private static final Logger logger = LoggerFactory.getLogger(RelationS3Services.class);
//
//    @Value("${s3.access.key:AE8I5VFYQ11RONY8U4L4}")
//    private String access_key;
//
//    @Value("${s3.secret.key:oIEcbPOFQUEH7Cng3OQnLl1mlbO3PlTA5uTlB5fV}")
//    private String secret_key;
//
//    @Value("${s3.end.point:10.128.3.22:8000}")
//    private String endpoint;
//
//    @Value("${s3.upload.split.length:104857600}")
//    private String splitLength;
//
//    private static AmazonS3 conn;
//
//    private synchronized AmazonS3 getConn() {
//        if (conn == null) {
//            AWSCredentials credentials = new BasicAWSCredentials(access_key, secret_key);
//            ClientConfiguration connconfig = new ClientConfiguration();
//            connconfig.setProtocol(Protocol.HTTP);
//
//            conn = new AmazonS3Client(credentials, connconfig);
//            getConn().setEndpoint(endpoint);
//            getConn().setS3ClientOptions(
//                    S3ClientOptions.builder().setPathStyleAccess(true).disableChunkedEncoding().build());
//        }
//        return conn;
//    }
//
//    public String uploadAWSFile(String date, String fileUrl) throws IOException {
//        String datePath = this.handleDate(date);
//
//        String bucketName = "video" + datePath.substring(0, 4);
//        s3_createBucket(bucketName);
//
//        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
//        String key = datePath + "/" + fileName;
//        String downLoadPath = "downLoadS3File/" + bucketName + "/" + key.replace("/", "~~");
//
//        URL url = new URL(fileUrl);
//        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//
//        try (InputStream is = conn.getInputStream()) {
//            long len = conn.getContentLength();
//            long splitLengthL = Long.parseLong(splitLength);
//            if (len < splitLengthL) {
//                s3PutobjBystream(bucketName, key, is, len);
//
//                return downLoadPath;
//            }
//            completeMultipartUpload(bucketName, key, is, len);
//        } catch (AmazonServiceException e) {
//            logger.error("S3上传文件失败: ", e);
//        }
//
//        return downLoadPath;
//    }
//
//    private String handleDate(String date) {
//        if (StringUtils.isEmpty(date)) {
//            Calendar calendar = Calendar.getInstance();
//            int year = calendar.get(Calendar.YEAR);
//            int month = calendar.get(Calendar.MONTH) + 1;
//            int day = calendar.get(Calendar.DAY_OF_MONTH);
//            return year + "/" + month + "/" + day;
//        }
//        String year = date.substring(0, 4);
//        String month = date.substring(4, 6);
//        String day = date.substring(6, 8);
//
//        return year + "/" + month + "/" + day;
//    }
//
//    private void s3PutobjBystream(String bucketName, String key, InputStream fileStream, long fileSize) {
//        try {
//            ObjectMetadata objectMetadata = new ObjectMetadata();
//            objectMetadata.setContentLength(fileSize);
//
//            PutObjectRequest request =
//                    new PutObjectRequest(
//                            bucketName,
//                            key,
//                            new ByteArrayInputStream(IOUtils.toByteArray(fileStream)),
//                            objectMetadata);
//            getConn().putObject(request);
//        } catch (AmazonServiceException ase) {
//            logger.info("s3_svr_error_message:{}, s3_svr_status_code:{}, s3_svr_error_code:{}", ase.getMessage(), ase.getStatusCode(), ase.getErrorCode());
//        } catch (AmazonClientException ace) {
//            logger.info("s3_clt_error_message:{}", ace.getMessage());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void completeMultipartUpload(String bucketName, String keyName, InputStream fileStream, long fileSize) {
//        try {
//            TransferManager tm = TransferManagerBuilder.standard()
//                    .withS3Client(getConn())
//                    .build();
//
//            ObjectMetadata objectMetadata = new ObjectMetadata();
//            objectMetadata.setContentLength(fileSize);
//
//            PutObjectRequest request =
//                    new PutObjectRequest(bucketName,
//                            keyName,
//                            new ByteArrayInputStream(IOUtils.toByteArray(fileStream)),
//                            objectMetadata);
//
//            Upload upload = tm.upload(request);
//            upload.waitForCompletion();
//        } catch (InterruptedException | SdkClientException | IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void s3_createBucket(String bucketname) {
//        if (getConn().doesBucketExist(bucketname) == true) {
//            System.out.println(bucketname + " already exist!");
//            return;
//        }
//        System.out.println("creating " + bucketname + " ...");
//        getConn().createBucket(bucketname);
//        System.out.println(bucketname + " has been created!");
//    }
//
//
//    public byte[] s3_get_obj(String bucketname, String objectname) {
//        BufferedInputStream in_stream = null;
//        ByteArrayOutputStream out_stream = new ByteArrayOutputStream();
//        try {
//            S3Object s3Object = getConn().getObject(new GetObjectRequest(bucketname, objectname));
//            System.out.println("s3_content-type :" + s3Object.getObjectMetadata().getContentType());
//            System.out.println("s3_etag : " + s3Object.getObjectMetadata().getETag());
//            System.out.println("s3_content_length:" + s3Object.getObjectMetadata().getContentLength());
//
//            in_stream = new BufferedInputStream(s3Object.getObjectContent());
//
//            byte[] buffer = new byte[1024];
//            int s3Offset;
//            try {
//                while ((s3Offset = in_stream.read(buffer)) != -1) {
//                    out_stream.write(buffer, 0, s3Offset);
//                    out_stream.flush();
//                }
//            } catch (IOException e) {
//                logger.info(e.toString());
//            }
//
//            return out_stream.toByteArray();
//
//        } catch (AmazonClientException ase) {
//            throw ase;
//        } finally {
//            if (null != in_stream) {
//                try {
//                    in_stream.close();
//                } catch (IOException e) {
//                    //e.printStackTrace();
//                    logger.info(e.toString());
//                }
//            }
//            try {
//                out_stream.close();
//            } catch (IOException e) {
//                logger.info(e.toString());
//            }
//        }
//    }
//
//}