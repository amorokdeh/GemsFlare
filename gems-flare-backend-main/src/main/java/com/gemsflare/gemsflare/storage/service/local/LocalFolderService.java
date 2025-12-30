package com.gemsflare.gemsflare.storage.service.local;

import com.gemsflare.gemsflare.permission.service.PermissionService;
import com.gemsflare.gemsflare.security.JwtUtil;
import com.gemsflare.gemsflare.storage.service.StorageFolderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;

@Service
@Profile("local")
public class LocalFolderService implements StorageFolderService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PermissionService permissionService;

    @Value("${file.storage.location:uploads}")
    private String storageLocation;

    public ResponseEntity<String> deleteItemFolder(HttpServletRequest request, String folderName) {
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

        Path folderPath = Paths.get(storageLocation, "items", folderName);

        if (!Files.exists(folderPath)) {
            return ResponseEntity.status(404).body("Folder not found.");
        }

        try {
            Files.walk(folderPath)
                    .sorted((p1, p2) -> p2.compareTo(p1))
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to delete: " + path, e);
                        }
                    });

            return ResponseEntity.ok("Folder deleted successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Folder deletion failed: " + e.getMessage());
        }
    }
}