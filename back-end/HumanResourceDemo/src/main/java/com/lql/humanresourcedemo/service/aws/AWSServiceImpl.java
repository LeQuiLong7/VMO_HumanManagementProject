package com.lql.humanresourcedemo.service.aws;


import com.lql.humanresourcedemo.dto.model.employee.OnlyAvatar;
import com.lql.humanresourcedemo.exception.model.aws.AWSException;
import com.lql.humanresourcedemo.exception.model.employee.EmployeeException;
import com.lql.humanresourcedemo.exception.model.file.FileException;
import com.lql.humanresourcedemo.repository.employee.EmployeeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;

import static com.lql.humanresourcedemo.util.AWSUtil.BUCKET_NAME;
import static com.lql.humanresourcedemo.util.FileUtil.*;

@Service
public class AWSServiceImpl implements AWSService {
    private final S3Client s3Client;
    private final EmployeeRepository employeeRepository;

    private final String REGION;
    public AWSServiceImpl(S3Client s3Client, EmployeeRepository employeeRepository, @Value("${spring.cloud.aws.region.static}") String region) {
        this.s3Client = s3Client;
        this.employeeRepository = employeeRepository;
        this.REGION = region;
    }

    @Override
    @Transactional

    public String uploadAvatar(Long employeeId, MultipartFile file) {
        // only allow some image type for avatar
        String fileExtension = getFileExtension(file.getOriginalFilename()).toLowerCase();
        if (!supportAvatarExtension(fileExtension)) {
            throw new FileException("Not support " + fileExtension + " file for avatar - supported types: "  + supportImageExtension.toString());
        }
        // delete the old avatar if exists, throw exception if the employee does not exist
        employeeRepository.findById(employeeId, OnlyAvatar.class)
                .ifPresentOrElse(avatar -> {
                    if(!avatar.avatarUrl().isBlank()) {
                        deleteOldAvatar(avatar);
                    }
                }, () -> {
                    throw new EmployeeException(employeeId);
                });

        // the key for avatar will be: image/{employeeId}.{fileExtention}
        String objectKey = String.format("image/%s.%s", employeeId, fileExtension);

        String avatarUrl =  uploadFile(file, BUCKET_NAME, objectKey);
        // set the avatar url of employee to the newly uploaded
        employeeRepository.updateAvatarURLById(employeeId, avatarUrl);

        return avatarUrl;
    }

    @Override
    @Transactional
    public String uploadReport( File file, String key) {
        return uploadFile(file, BUCKET_NAME, key);
    }



    // form the full s3 address from the bucket name, region and object key
    private String getUrlForObject(String bucketName, String region, String objectKey) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, objectKey);
    }



    private void deleteFile(String bucketName, String key) {
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        try {
            s3Client.deleteObject(deleteRequest);
        } catch (Exception ex) {
            throw new AWSException("Error deleting file");
        }
    }

    private String uploadFile(File file, String bucketName, String key) {
        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        try {
            s3Client.putObject(putRequest, RequestBody.fromFile(file));
        } catch (Exception ex) {
            throw new AWSException("Error uploading file");
        }
        return getUrlForObject(bucketName, REGION, key);
    }



    private String uploadFile(MultipartFile file, String bucketName, String key) {
        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        try {
            s3Client.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (Exception ex) {
            throw new AWSException("Error uploading file");
        }
        return getUrlForObject(bucketName, REGION, key);
    }




    private void deleteOldAvatar(OnlyAvatar avatar) {

        // example url: https://human-management-project.s3.ap-southeast-1.amazonaws.com/image/1.jpg
        // remove the https:// part
        // remain: human-management-project.s3.ap-southeast-1.amazonaws.com/image/1.jpg
        String trimmedUrl = avatar.avatarUrl().substring(8);
        // Split the remaining URL by ".s3."
        // result: parts[0]: human-management-project : bucket name
        //         parts[1]: ap-southeast-1.amazonaws.com/image/1.jpg : region and object id
        String[] parts = trimmedUrl.split("\\.s3\\.");

        // Extract bucket name and object key
        String bucketName = parts[0];
        // Extract the object key
        String objectKey = parts[1].substring(parts[1].indexOf("/") + 1);

        deleteFile(bucketName, objectKey);
    }


}