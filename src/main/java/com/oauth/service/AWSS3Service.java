package com.oauth.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.oauth.exception.FileNotFoundException;
import com.oauth.interfaces.AWSS3Interface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class AWSS3Service implements AWSS3Interface {
    private static final Logger LOGGER = LoggerFactory.getLogger(AWSS3Service.class);
    private final AmazonS3 amazonS3;
    @Value("${aws.s3.bucket-name}")
    private String bucketName;
    @Value("${aws.s3.region}")
    private String region;

    public AWSS3Service(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    @Override
    public String uploadFileToS3(MultipartFile file) {
        try {
            String newFilename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());
            amazonS3.putObject(new PutObjectRequest(bucketName, newFilename, file.getInputStream(), metadata));
            return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + newFilename;
        } catch (IOException e) {
            LOGGER.error("Error al cargar el archivo a S3", e);
            throw new RuntimeException("Error al cargar el archivo a S3", e);
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
        if (!doesObjectExist(key)) {
            throw new FileNotFoundException("File " + key + " does not exist");
        }
        S3Object object = amazonS3.getObject(bucketName, key);
        return object.getObjectContent();
    }


    public boolean doesObjectExist(String key) {
        try {
            return amazonS3.doesObjectExist(bucketName, key);
        } catch (AmazonS3Exception e) {

            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
