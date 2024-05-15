package com.lql.humanresourcedemo.service.aws;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface AWSService {
    String getUrlForObject(String bucketName, String region, String objectKey);
    String uploadFile(MultipartFile file, String bucketName, String key);
    void deleteFile(String bucketName, String key);
    String uploadFile(File file, String bucketName, String key);
}
