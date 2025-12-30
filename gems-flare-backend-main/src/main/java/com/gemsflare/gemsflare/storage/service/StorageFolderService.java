package com.gemsflare.gemsflare.storage.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface StorageFolderService {
    ResponseEntity<String> deleteItemFolder(HttpServletRequest request, String folderName);
}