package com.gemsflare.gemsflare.storage.controller;

import com.gemsflare.gemsflare.storage.service.StorageObjectService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/object")
public class ObjectController {

    @Autowired
    private StorageObjectService objectService;

    @Operation(summary = "Upload an object file to a folder")
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadObject(
            HttpServletRequest request,
            @RequestParam("file") MultipartFile object,
            @RequestParam("folder") String folder) {
        return objectService.uploadObjectToFolder(request, object, folder, null);
    }

    @Operation(summary = "Delete an object file from a folder")
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteObject(
            HttpServletRequest request,
            @RequestParam("folder") String folder,
            @RequestParam("fileName") String fileName) {
        return objectService.deleteObject(request, folder, fileName);
    }

    @Operation(summary = "Replace an existing object with a new one (same name)")
    @PutMapping(value = "/edit", consumes = "multipart/form-data")
    public ResponseEntity<String> editObject(
            HttpServletRequest request,
            @RequestParam("file") MultipartFile object,
            @RequestParam("folder") String folderName,
            @RequestParam("fileName") String oldFileName) {
        return objectService.editObject(request, object, folderName, oldFileName, null);
    }
}