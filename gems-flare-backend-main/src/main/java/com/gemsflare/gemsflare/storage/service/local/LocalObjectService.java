package com.gemsflare.gemsflare.storage.service.local;

import com.gemsflare.gemsflare.permission.service.PermissionService;
import com.gemsflare.gemsflare.security.JwtUtil;
import com.gemsflare.gemsflare.storage.service.StorageObjectService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@Service
@Profile("local")
public class LocalObjectService implements StorageObjectService {

    @Autowired
    private PermissionService permissionService;
    @Autowired
    private JwtUtil jwtUtil;

    @Value("${file.storage.location:uploads}")
    private String storageLocation;

    public ResponseEntity<String> uploadObjectToFolder(HttpServletRequest request, MultipartFile object, String folder, String fileName) {
        try {
            if (folder.endsWith("/")) {
                folder = folder.substring(0, folder.length() - 1);
            }

            String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            String route = "/item/" + folder;

            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Unauthorized: No token provided");
            }

            String token = authorizationHeader.substring(7);
            if (!jwtUtil.isTokenValid(token)) {
                return ResponseEntity.status(401).body("Unauthorized: Invalid token");
            }

            if (!permissionService.hasPermission(request, route)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Permission required");
            }

            if (fileName == null || fileName.isEmpty()) {
                fileName = generateRandomFileName(object);
            }

            Path folderPath = Paths.get(storageLocation, "items", folder);
            Files.createDirectories(folderPath);

            Path filePath = folderPath.resolve(fileName);
            Files.copy(object.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String objectUrl = "/files/items/" + folder + "/" + fileName;
            return ResponseEntity.ok(objectUrl);

        } catch (IOException e) {
            return ResponseEntity.status(500).body("Object upload failed: " + e.getMessage());
        }
    }

    public ResponseEntity<String> deleteObject(HttpServletRequest request, String folderName, String fileName) {
        try {
            Path filePath = Paths.get(storageLocation, "items", folderName, fileName);
            boolean deleted = Files.deleteIfExists(filePath);

            if (deleted) {
                return ResponseEntity.ok("Object deleted successfully.");
            } else {
                return ResponseEntity.status(404).body("Object not found.");
            }
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Object deletion failed: " + e.getMessage());
        }
    }

    public ResponseEntity<String> editObject(HttpServletRequest request, MultipartFile object, String folderName, String oldFilePath, String newFileName) {
        try {
            if (newFileName == null || newFileName.isEmpty()) {
                newFileName = generateRandomFileName(object);
            }

            Path folderPath = Paths.get(storageLocation, "items", folderName);
            Files.createDirectories(folderPath);

            Path newFilePath = folderPath.resolve(newFileName);
            Files.copy(object.getInputStream(), newFilePath, StandardCopyOption.REPLACE_EXISTING);

            String oldFileName = getFileNameFromLink(oldFilePath);
            Path oldPath = folderPath.resolve(oldFileName);
            Files.deleteIfExists(oldPath);

            String newObjectUrl = "/files/items/" + folderName + "/" + newFileName;
            return ResponseEntity.ok("Object updated successfully: " + newObjectUrl);

        } catch (IOException e) {
            return ResponseEntity.status(500).body("Object update failed: " + e.getMessage());
        }
    }

    public String generateRandomFileName(MultipartFile object) {
        String originalName = object.getOriginalFilename();
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }
        return System.currentTimeMillis() + "-" + (long) (Math.random() * 1_000_000) + extension;
    }

    public String getFileNameFromLink(String fullUrl) {
        if (fullUrl != null && fullUrl.contains("/")) {
            return fullUrl.substring(fullUrl.lastIndexOf("/") + 1);
        } else {
            throw new IllegalArgumentException("Invalid local URL: " + fullUrl);
        }
    }
}