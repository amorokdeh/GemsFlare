package com.gemsflare.gemsflare.storage.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface StorageObjectService {
    String generateRandomFileName(MultipartFile file);
    ResponseEntity<String> uploadObjectToFolder(HttpServletRequest request, MultipartFile object, String folder, String fileName);
    ResponseEntity<String> deleteObject(HttpServletRequest request, String folderName, String fileName);
    ResponseEntity<String> editObject(HttpServletRequest request, MultipartFile object, String folderName, String oldFilePath, String newFileName);
    String getFileNameFromLink(String url);
}