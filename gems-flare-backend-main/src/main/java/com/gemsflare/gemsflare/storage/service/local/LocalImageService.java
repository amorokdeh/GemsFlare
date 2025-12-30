package com.gemsflare.gemsflare.storage.service.local;

import com.gemsflare.gemsflare.permission.service.PermissionService;
import com.gemsflare.gemsflare.security.JwtUtil;
import com.gemsflare.gemsflare.storage.service.StorageImageService;
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
public class LocalImageService implements StorageImageService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PermissionService permissionService;

    @Value("${file.storage.location:uploads}")
    private String storageLocation;

    public ResponseEntity<String> uploadImageToFolder(HttpServletRequest request, MultipartFile image, String folder, String fileName) {
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
                fileName = generateRandomFileName(image);
            }

            Path folderPath = Paths.get(storageLocation, "items", folder);
            Files.createDirectories(folderPath);

            Path filePath = folderPath.resolve(fileName);
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String imageUrl = "/files/items/" + folder + "/" + fileName;
            return ResponseEntity.ok(imageUrl);

        } catch (IOException e) {
            return ResponseEntity.status(500).body("Image upload failed: " + e.getMessage());
        }
    }

    public ResponseEntity<String> deleteImage(HttpServletRequest request, String folderName, String fileName) {
        try {
            Path filePath = Paths.get(storageLocation, "items", folderName, fileName);
            Files.deleteIfExists(filePath);
            return ResponseEntity.ok("Image deleted successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Image deletion failed: " + e.getMessage());
        }
    }

    public ResponseEntity<String> editImage(HttpServletRequest request, MultipartFile image, String folderName, String oldFilePath, String newFileName) {
        try {
            if (newFileName == null || newFileName.isEmpty()) {
                newFileName = generateRandomFileName(image);
            }

            Path folderPath = Paths.get(storageLocation, "items", folderName);
            Files.createDirectories(folderPath);

            Path newFilePath = folderPath.resolve(newFileName);
            Files.copy(image.getInputStream(), newFilePath, StandardCopyOption.REPLACE_EXISTING);

            String oldFileName = getFileNameFromLink(oldFilePath);
            Path oldPath = folderPath.resolve(oldFileName);
            Files.deleteIfExists(oldPath);

            String newImageUrl = "/files/items/" + folderName + "/" + newFileName;
            return ResponseEntity.ok("Image updated successfully: " + newImageUrl);

        } catch (IOException e) {
            return ResponseEntity.status(500).body("Image update failed: " + e.getMessage());
        }
    }

    public String generateRandomFileName(MultipartFile file) {
        String originalName = file.getOriginalFilename();
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