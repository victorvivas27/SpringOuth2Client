package com.oauth.controller;

import com.oauth.service.AWSS3Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/s3")

public class UploadFileController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadFileController.class);
    private final AWSS3Service awss3Service;
    @Value("${aws.s3.bucket-name}")
    private String bucketName;
    @Value("${aws.s3.region}")
    private String region;

    public UploadFileController(AWSS3Service awss3Service) {
        this.awss3Service = awss3Service;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestPart(value = "file") MultipartFile file) {// Subir el archivo a S3 y obtener el nuevo nombre
        String fileName = awss3Service.uploadFileToS3(file);
        String fileUrl = "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + fileName;
        Map<String, String> response = new HashMap<>();
        response.put("message", "Archivo subido correctamente a S3");
        response.put("fileUrl", fileUrl);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/listeFile")
    public ResponseEntity<List<String>> listFile() {
        return new ResponseEntity<List<String>>(awss3Service.getObjectFromS3(), HttpStatus.OK);
    }

    @GetMapping("/download")
    public ResponseEntity<?> download(@RequestParam("key") String key) {
        InputStreamResource resource = new InputStreamResource(awss3Service.downloadFile(key));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + key + "\"")
                .body(resource);
    }
}
