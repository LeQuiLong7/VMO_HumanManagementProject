package com.lql.humanresourcedemo.service.aws;

import com.lql.humanresourcedemo.dto.model.employee.OnlyAvatar;
import com.lql.humanresourcedemo.exception.model.aws.AWSException;
import com.lql.humanresourcedemo.exception.model.employee.EmployeeException;
import com.lql.humanresourcedemo.exception.model.file.FileException;
import com.lql.humanresourcedemo.repository.employee.EmployeeRepository;
import com.lql.humanresourcedemo.util.AWSUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AWSServiceImplTest {

    @Mock
    private S3Client s3Client;
    @Mock
    private EmployeeRepository employeeRepository;
    private final String MOCK_REGION = "mock-region";
    private AWSService awsService;

    @BeforeEach
    void setUp() {
        awsService = new AWSServiceImpl(s3Client, employeeRepository, MOCK_REGION);
    }

    @Test
    void uploadAvatar_success_NoOldAvatar() {
        Long employeeId = 1L;
        String fileName = "avatar.jpg";
        MockMultipartFile file = new MockMultipartFile("file", fileName, "image/jpeg", "content".getBytes());
        String expectedUrl = String.format("https://%s.s3.%s.amazonaws.com/image/%s.jpg", AWSUtil.BUCKET_NAME, MOCK_REGION, employeeId);
        OnlyAvatar onlyAvatar = new OnlyAvatar("");


        when(employeeRepository.findById(employeeId, OnlyAvatar.class)).thenReturn(Optional.of(onlyAvatar));

        String actualUrl = awsService.uploadAvatar(employeeId, file);

        assertEquals(expectedUrl, actualUrl);
        verify(employeeRepository, times(1)).updateAvatarURLById(employeeId, expectedUrl);
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        verify(s3Client, times(0)).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void uploadAvatar_success_ExistsOldAvatar() {
        Long employeeId = 1L;
        String fileName = "avatar.jpg";
        MockMultipartFile file = new MockMultipartFile("file", fileName, "image/jpeg", "content".getBytes());
        String expectedUrl = String.format("https://%s.s3.%s.amazonaws.com/image/%s.jpg", AWSUtil.BUCKET_NAME, MOCK_REGION, employeeId);

        OnlyAvatar onlyAvatar = new OnlyAvatar("https://demo-bucket.s3.ap-southeast-1.amazonaws.com/image/1.jpg");


        when(employeeRepository.findById(employeeId, OnlyAvatar.class)).thenReturn(Optional.of(onlyAvatar));

        String actualUrl = awsService.uploadAvatar(employeeId, file);

        assertEquals(expectedUrl, actualUrl);
        verify(employeeRepository, times(1)).updateAvatarURLById(employeeId, expectedUrl);
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        verify(s3Client, times(1)).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void uploadAvatar_unsupportedExtension() {
        Long employeeId = 1L;
        String fileName = "avatar.txt";
        MockMultipartFile file = new MockMultipartFile("file", fileName, "text/plain", "text content".getBytes());

        assertThrows(FileException.class, () -> awsService.uploadAvatar(employeeId, file));
    }

    @Test
    void uploadAvatar_employeeNotFound() {
        Long employeeId = 1L;
        String fileName = "avatar.jpg";
        MockMultipartFile file = new MockMultipartFile("file", fileName, "image/jpeg", "image content".getBytes());

        when(employeeRepository.findById(employeeId, OnlyAvatar.class)).thenReturn(Optional.empty());

        assertThrows(EmployeeException.class, () -> awsService.uploadAvatar(employeeId, file));
    }

    @Test
    void uploadAvatar_errorUploadingFile() {
        Long employeeId = 1L;
        String fileName = "avatar.jpg";
        MockMultipartFile file = new MockMultipartFile("file", fileName, "image/jpeg", "image content".getBytes());

        OnlyAvatar onlyAvatar = new OnlyAvatar("");

        when(employeeRepository.findById(employeeId, OnlyAvatar.class)).thenReturn(Optional.of(onlyAvatar));
        doThrow(new AWSException("Error uploading file")).when(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));

        assertThrows(AWSException.class, () -> awsService.uploadAvatar(employeeId, file));
    }
}