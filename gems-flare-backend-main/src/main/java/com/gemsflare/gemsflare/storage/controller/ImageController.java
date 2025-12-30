package com.gemsflare.gemsflare.storage.controller;

import com.gemsflare.gemsflare.storage.service.StorageImageService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/image")
public class ImageController {

    @Autowired
    private StorageImageService imageService;

    @Operation(summary = "Upload an image file to a folder")
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadImage(
            HttpServletRequest request,
            @RequestParam("file") MultipartFile image,
            @RequestParam("folder") String folder) {
        return imageService.uploadImageToFolder(request, image, folder, null);
    }

    @Operation(summary = "Delete an image file from a folder")
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteImage(
            HttpServletRequest request,
            @RequestParam("folder") String folder,
            @RequestParam("fileName") String fileName) {
        return imageService.deleteImage(request, folder, fileName);
    }

    @Operation(summary = "Replace an existing image with a new one (same name)")
    @PutMapping(value = "/edit", consumes = "multipart/form-data")
    public ResponseEntity<String> editImage(
            HttpServletRequest request,
            @RequestParam("file") MultipartFile image,
            @RequestParam("folder") String folderName,
            @RequestParam("fileName") String oldFileName) {
        return imageService.editImage(request, image, folderName, oldFileName, null);
    }
}