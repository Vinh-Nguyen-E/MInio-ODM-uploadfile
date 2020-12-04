package com.example.springboot_minio.service;

import io.minio.*;

import io.minio.http.Method;
import io.minio.messages.Bucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Service
public class MinIOObjectService {

    @Value("${minio.bucket.name}")
    private String bucketName;

    @Autowired
    MinioClient minioClient;

    public void uploadFile (InputStream file, String name, Long size, String contentType) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(name)
                            .stream(file, size, -1)
                            .contentType(contentType)
                            .build());
        } catch (Exception e) {
            throw new RuntimeException("can't upload file", e);
        }
    }

    public Resource getFile(String name) {
        try {
            InputStream inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(name)
                        .build());
            Resource resource = new InputStreamResource(inputStream);
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException("file does not exist", e);
        }
    }

    public String getObjUrl(String name) {
        try {
            String url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(name)
                            .expiry(7, TimeUnit.DAYS)
                            .build()
            );
            System.out.println(url);
            return url;
        } catch (Exception e) {
            throw new RuntimeException("can't get url obj", e);
        }
    }

}
