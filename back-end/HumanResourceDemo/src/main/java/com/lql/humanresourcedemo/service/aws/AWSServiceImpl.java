package com.lql.humanresourcedemo.service.aws;


import com.lql.humanresourcedemo.exception.model.aws.AWSException;
import com.lql.humanresourcedemo.utility.ContextUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AWSServiceImpl implements AWSService {
    private final S3Client s3Client;

    @Override
    public String getUrlForObject(String bucketName, String region, String objectKey) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, objectKey);
    }


    @Override
    public String uploadFile(MultipartFile file, String bucketName, String key) {
        PutObjectRequest build = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        try {
            s3Client.putObject(build, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        } catch (Exception ex) {
            throw new AWSException("Error uploading file");
        }

        return key;
    }

    @Override
    public String uploadFile(File file, String bucketName, String key) {
        PutObjectRequest build = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        try {
            s3Client.putObject(build, RequestBody.fromFile(file));
        } catch (Exception e) {
            throw new AWSException("Error uploading file");

        }
        return key;
    }


}


//
//    public Resource downloadFile( String bucketName, String key) throws IOException {
//        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
//                .bucket(bucketName)
//                .key(key)
//                .build();
//        ResponseInputStream<GetObjectResponse> object = s3Client.getObject(getObjectRequest);
//
//        return new ByteArrayResource(object.readAllBytes());
//    }