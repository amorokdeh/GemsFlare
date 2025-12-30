package com.gemsflare.gemsflare.storage.service.gcs;

import com.gemsflare.gemsflare.permission.service.PermissionService;
import com.gemsflare.gemsflare.security.JwtUtil;
import com.gemsflare.gemsflare.storage.service.StorageObjectService;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
@Profile("prod")
public class GCSObjectService implements StorageObjectService {

    @Autowired
    private PermissionService permissionService;
    @Autowired
    private JwtUtil jwtUtil;

    private final String bucketName = "gemsflare-src";

    private final Storage storage;

    public GCSObjectService() throws IOException {
        InputStream keyStream = getClass().getResourceAsStream("/gcs-key.json");
        storage = StorageOptions.newBuilder()
                .setCredentials(ServiceAccountCredentials.fromStream(keyStream))
                .build()
                .getService();
    }

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

            if(fileName == null || fileName.isEmpty()) {
                fileName = generateRandomFileName(object);
            }

            String filePath = "items/" + folder + "/" + fileName;

            BlobId blobId = BlobId.of(bucketName, filePath);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(object.getContentType())
                    .build();

            storage.create(blobInfo, object.getBytes());

            String imageUrl = String.format("https://storage.googleapis.com/%s/%s", bucketName, filePath);
            return ResponseEntity.ok(imageUrl);

        } catch (IOException e) {
            return ResponseEntity.status(500).body("Object upload failed: " + e.getMessage());
        }
    }

    public ResponseEntity<String> deleteObject(HttpServletRequest request, String folderName, String fileName) {
        try {
            if (folderName.endsWith("/")) {
                folderName = folderName.substring(0, folderName.length() - 1);
            }

            String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            String route = "/item/" + folderName;

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

            String objectName = "items/" + fileName;
            BlobId blobId = BlobId.of(bucketName, objectName);
            boolean deleted = storage.delete(blobId);

            if (deleted) {
                return ResponseEntity.ok("Object deleted successfully.");
            } else {
                return ResponseEntity.status(404).body("Object not found.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Object deletion failed: " + e.getMessage());
        }
    }

    public ResponseEntity<String> editObject(HttpServletRequest request, MultipartFile object, String folderName, String oldFilePath, String newFileName) {
        try {
            if (folderName.endsWith("/")) {
                folderName = folderName.substring(0, folderName.length() - 1);
            }

            String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            String route = "/item/" + folderName;

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

            if(newFileName == null || newFileName.isEmpty()) {
                newFileName = generateRandomFileName(object);
            }

            String newFilePath = "items/" + folderName + "/" + newFileName;

            BlobId newBlobId = BlobId.of(bucketName, newFilePath);
            BlobInfo blobInfo = BlobInfo.newBuilder(newBlobId)
                    .setContentType(object.getContentType())
                    .build();

            storage.create(blobInfo, object.getBytes());

            oldFilePath = "items/" + getFileNameFromLink(oldFilePath);

            BlobId blobId = BlobId.of(bucketName, oldFilePath);
            boolean deleted = storage.delete(blobId);

            if (!deleted) {
                return ResponseEntity.status(404).body("Object to replace not found.");
            }

            return ResponseEntity.ok("Object updated successfully: https://storage.googleapis.com/" + bucketName + "/" + oldFilePath);
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
        return String.valueOf((long) (Math.random() * 1_000_000_000_000L)) + extension;
    }

    public String getFileNameFromLink(String fullUrl) {
        String prefix = "https://storage.googleapis.com/gemsflare-src/items/";
        if (fullUrl != null && fullUrl.startsWith(prefix)) {
            return fullUrl.substring(prefix.length());
        } else {
            throw new IllegalArgumentException("Invalid GCS URL: " + fullUrl);
        }
    }

}