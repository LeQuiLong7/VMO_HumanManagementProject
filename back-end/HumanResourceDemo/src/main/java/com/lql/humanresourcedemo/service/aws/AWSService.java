package com.lql.humanresourcedemo.service.aws;


import com.lql.humanresourcedemo.utility.AWSUtility;
import com.lql.humanresourcedemo.utility.FileUtility;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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

import java.io.IOException;

import static com.lql.humanresourcedemo.utility.AWSUtility.BUCKET_NAME;

@Service
@RequiredArgsConstructor
public class AWSService {
    private final S3Client s3Client;

    public Resource downloadFile( String bucketName, String key) throws IOException {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        ResponseInputStream<GetObjectResponse> object = s3Client.getObject(getObjectRequest);
//        s3Client.get
        return new ByteArrayResource(object.readAllBytes());
    }

    public String uploadFile(MultipartFile file, String bucketName, String key) throws IOException {
        PutObjectRequest build = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        s3Client.putObject(build, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        return key;
    }
}
