package com.lql.humanresourcedemo.service.aws;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AWSServiceTest {

    @Mock
    private S3Client s3Client;

    private AWSService awsService;

    @BeforeEach
    void setUp() {
        awsService = new AWSServiceImpl(s3Client);
    }

    @Test
    void getUrlForObjectTest() {
        String bucketName = "mock-bucket-name";
        String key = "mock-key";
        String region = "mock-region";

        assertEquals(String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, key), awsService.getUrlForObject(bucketName, region, key));

    }

    @Test
    void testUploadMultipartFile() {
        String bucketName = "mock-bucket-name";
        String key = "mock-key";
        MultipartFile file = new MockMultipartFile("demo.txt", "demo".getBytes());

        assertEquals(key, awsService.uploadFile(file, bucketName, key));

        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void testUploadFile() throws IOException {
        String bucketName = "mock-bucket-name";
        String key = "mock-key";
        File file = File.createTempFile("demo", "txt");

        assertEquals(key, awsService.uploadFile(file, bucketName, key));

        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }
}