package com.lql.humanresourcedemo.service.aws;

import com.lql.humanresourcedemo.exception.model.aws.AWSException;
import org.junit.jupiter.api.BeforeAll;
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

import static jakarta.servlet.RequestDispatcher.ERROR_MESSAGE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.postgresql.hostchooser.HostRequirement.any;

@ExtendWith(MockitoExtension.class)
class AWSServiceTest {

    private final String MOCK_BUCKET_NAME = "mock-bucket-name";
    private final String MOCK_KEY = "mock-key";
    private final String MOCK_REGION = "mock-region";


    @Mock
    private S3Client s3Client;

//    @Mock
    private AWSService awsService;

    @BeforeEach
    void setUp() {
        awsService = new AWSServiceImpl(s3Client);
    }

    public AWSServiceTest() {
        awsService = new AWSServiceImpl(s3Client);
    }

    @Test
    void getUrlForObjectTest() {


        assertEquals(String.format("https://%s.s3.%s.amazonaws.com/%s", MOCK_BUCKET_NAME, MOCK_REGION, MOCK_KEY),
                awsService.getUrlForObject(MOCK_BUCKET_NAME, MOCK_REGION, MOCK_KEY));

    }

    @Test
    void testUploadMultipartFile_Success() {

        MultipartFile file = new MockMultipartFile("demo.txt", "demo".getBytes());

        assertEquals(MOCK_KEY, awsService.uploadFile(file, MOCK_BUCKET_NAME, MOCK_KEY));

        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void testUploadMultipartFile_Fail() {
        MultipartFile file = new MockMultipartFile("demo.txt", "demo".getBytes());
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(new AWSException(ERROR_MESSAGE));
        assertThrows(
                AWSException.class,
                () -> awsService.uploadFile(file, MOCK_BUCKET_NAME, MOCK_KEY),
                ERROR_MESSAGE
        );
    }

    @Test
    void testUploadFile_Success() throws IOException {

        File file = File.createTempFile("demo", "txt");

        assertEquals(MOCK_KEY, awsService.uploadFile(file, MOCK_BUCKET_NAME, MOCK_KEY));

        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void testUploadFile_Fail() throws Exception{

        File file = File.createTempFile("demo", "txt");

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(new AWSException(ERROR_MESSAGE));

        assertThrows(
                AWSException.class,
                () -> awsService.uploadFile(file, MOCK_BUCKET_NAME, MOCK_KEY),
                ERROR_MESSAGE
        );
    }

}