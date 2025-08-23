package com.example.billing.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*")
public class FileUploadController {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @PostMapping("/upload/signature")
    public ResponseEntity<String> uploadSignature(@RequestParam("file") MultipartFile file) {
        try {
            // Convert file to base64
            byte[] fileBytes = file.getBytes();
            String base64String = java.util.Base64.getEncoder().encodeToString(fileBytes);
            
            // Return the base64 encoded string
            return ResponseEntity.ok(base64String);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Failed to process file: " + e.getMessage());
        }
    }
}
