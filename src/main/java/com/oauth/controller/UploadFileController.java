package com.oauth.controller;

import com.oauth.service.AWSS3Service;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/s3")
@AllArgsConstructor
public class UploadFileController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadFileController.class);
    private final AWSS3Service awss3Service;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestPart(value = "file") MultipartFile file) {
        awss3Service.uploadFileToS3(file);
        String response = "El archivo" + file.getOriginalFilename() + "fue cargado correctamente a S3";
        return new ResponseEntity<String>(response, HttpStatus.OK);
    }

    @GetMapping("/listeFile")
    public ResponseEntity<List<String>> listFile() {
        return new ResponseEntity<List<String>>(awss3Service.getObjectFromS3(), HttpStatus.OK);
    }

    @GetMapping("/download")
    public ResponseEntity<?> download(@RequestParam("key") String key) {
        LOGGER.info("Attempting to download file with key: {}", key);
        if (!awss3Service.doesObjectExist(key)) {
            LOGGER.warn("File not found in S3: {}", key);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("File not found: " + key);
        }
        InputStream inputStream = awss3Service.downloadFile(key);
        InputStreamResource resource = new InputStreamResource(inputStream);
        String contentDisposition = "attachment; filename=\"" + key + "\"";
        MediaType contentType = getContentTypeFromExtension(key);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .contentType(contentType)
                .body(resource);
    }

    private MediaType getContentTypeFromExtension(String key) {
        if (key.endsWith(".png")) {
            return MediaType.IMAGE_PNG;
        } else if (key.endsWith(".jpg") || key.endsWith(".jpeg")) {
            return MediaType.IMAGE_JPEG;
        } else if (key.endsWith(".pdf")) {
            return MediaType.APPLICATION_PDF;
        } else {
            return MediaType.APPLICATION_OCTET_STREAM; // Tipo por defecto
        }
    }
}
