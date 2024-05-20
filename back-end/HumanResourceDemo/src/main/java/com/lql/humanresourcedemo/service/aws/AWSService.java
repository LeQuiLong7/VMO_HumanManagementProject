package com.lql.humanresourcedemo.service.aws;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface AWSService {

    String uploadAvatar(Long employeeId, MultipartFile file);
    String uploadReport( File file, String key);

}
