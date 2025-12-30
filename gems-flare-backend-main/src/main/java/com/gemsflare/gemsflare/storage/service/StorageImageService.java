package com.gemsflare.gemsflare.storage.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface StorageImageService {
    String generateRandomFileName(MultipartFile file);
    ResponseEntity<String> uploadImageToFolder(HttpServletRequest request, MultipartFile image, String folder, String fileName);
    ResponseEntity<String> deleteImage(HttpServletRequest request, String folderName, String fileName);
    ResponseEntity<String> editImage(HttpServletRequest request, MultipartFile image, String folderName, String oldFilePath, String newFileName);
    String getFileNameFromLink(String url);
}