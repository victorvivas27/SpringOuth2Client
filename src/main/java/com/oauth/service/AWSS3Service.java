package com.oauth.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.oauth.interfaces.AWSS3Interface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class AWSS3Service implements AWSS3Interface {
    private static final Logger LOGGER = LoggerFactory.getLogger(AWSS3Service.class);
    private final AmazonS3 amazonS3;
    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public AWSS3Service(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    @Override
    public void uploadFileToS3(MultipartFile file) {
        File uploadFile = new File(file.getOriginalFilename());
        try (FileOutputStream stream = new FileOutputStream(uploadFile)) {
            stream.write(file.getBytes());
            String newFilename = System.currentTimeMillis() + "_" + uploadFile.getName();
            PutObjectRequest request = new PutObjectRequest(bucketName, newFilename, uploadFile);
            amazonS3.putObject(request);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public List<String> getObjectFromS3() {
        ListObjectsV2Result result = amazonS3.listObjectsV2(bucketName);
        List<S3ObjectSummary> objects = result.getObjectSummaries();
        List<String> list = objects.stream().map(items -> {
            return items.getKey();
        }).toList();
        return list;
    }

    @Override
    public InputStream downloadFile(String key) {
        S3Object object = amazonS3.getObject(bucketName, key);
        return object.getObjectContent();
    }

    public boolean doesObjectExist(String key) {
        try {
            return amazonS3.doesObjectExist(bucketName, key);
        } catch (AmazonS3Exception e) {
            // Maneja cualquier error relacionado con S3
            System.err.println("Error checking if object exists in S3: " + e.getMessage());
            return false;
        } catch (Exception e) {
            // Maneja cualquier otro error inesperado
            System.err.println("Unexpected error: " + e.getMessage());
            return false;
        }
    }


}
