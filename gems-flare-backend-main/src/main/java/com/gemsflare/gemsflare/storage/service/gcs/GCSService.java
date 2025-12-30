package com.gemsflare.gemsflare.storage.service.gcs;

import com.gemsflare.gemsflare.permission.service.PermissionService;
import com.gemsflare.gemsflare.security.JwtUtil;
import com.gemsflare.gemsflare.storage.service.StorageFolderService;
import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
@Profile("prod")
public class GCSService implements StorageFolderService {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PermissionService permissionService;

    private final String bucketName = "gemsflare-src";

    private final Storage storage;

    public GCSService() throws IOException {
        InputStream keyStream = getClass().getResourceAsStream("/gcs-key.json");
        storage = StorageOptions.newBuilder()
                .setCredentials(ServiceAccountCredentials.fromStream(keyStream))
                .build()
                .getService();
    }

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

        Page<Blob> blobs = storage.list(bucketName, Storage.BlobListOption.prefix("items/" + folderName + "/"));

        boolean isFolderEmpty = true;

        for (Blob blob : blobs.iterateAll()) {
            storage.delete(blob.getBlobId());
            isFolderEmpty = false;
        }

        if (isFolderEmpty) {
            return ResponseEntity.status(404).body("Folder not found or already empty.");
        }

        return ResponseEntity.ok("Folder deleted successfully");
    }
}
