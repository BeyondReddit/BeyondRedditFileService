package com.example.fileservice;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;

@Service
public class FileStorageService {
    private final AmazonS3 s3Client;

    @Value("${app.s3.bucket}")
    private String bucketName;

    @Autowired
    public FileStorageService(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

//    public String uploadFile(MultipartFile file) {
//        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
//        ObjectMetadata metadata = new ObjectMetadata();
//        metadata.setContentType(file.getContentType());
//        metadata.setContentLength(file.getSize());
//
//        try {
//            s3Client.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), metadata));
//            return s3Client.getUrl(bucketName, fileName).toString();
//        } catch (IOException e) {
//            throw new RuntimeException("Error storing file to S3", e);
//        }
//    }
    public String uploadFile(MultipartFile file) {
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        try {
            s3Client.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), metadata));
            // Now that the file is uploaded, generate a pre-signed URL for direct access.
            return generatePresignedUrl(fileName);
        } catch (IOException e) {
            throw new RuntimeException("Error storing file to S3", e);
        }
    }

    public String generatePresignedUrl(String fileName) {
        // Set the presigned URL to expire after one hour.
        java.util.Date expiration = new java.util.Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 60; // 1 hour
        expiration.setTime(expTimeMillis);

        // Generate the presigned URL.
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucketName, fileName)
                        .withMethod(HttpMethod.GET)
                        .withExpiration(expiration);
        URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);

        return url.toString();
    }



    public String updateFile(MultipartFile file, String fileName) {
        try {
            // Overwrites the existing file with the new file content
            s3Client.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), null));
            return s3Client.getUrl(bucketName, fileName).toString();
        } catch (IOException e) {
            throw new RuntimeException("Error updating file in S3", e);
        }
    }

//    public String getFileUrl(String fileName) {
//        // Generates a pre-signed URL for downloading the file. The URL is valid for 1 hour.
//        java.util.Date expiration = new java.util.Date();
//        long expTimeMillis = expiration.getTime();
//        expTimeMillis += 1000 * 60 * 60; // 1 hour
//        expiration.setTime(expTimeMillis);
//
//        GeneratePresignedUrlRequest generatePresignedUrlRequest =
//                new GeneratePresignedUrlRequest(bucketName, fileName)
//                        .withMethod(HttpMethod.GET)
//                        .withExpiration(expiration);
//        URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
//
//        return url.toString();
//    }


    public void deleteFile(String fileName) {
        s3Client.deleteObject(bucketName, fileName);
    }


}
