package com.oauth.interfaces;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

public interface AWSS3Interface {
    String uploadFileToS3(MultipartFile file);

    List<String> getObjectFromS3();

    InputStream downloadFile(String key);
}
