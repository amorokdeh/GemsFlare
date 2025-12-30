package com.gemsflare.gemsflare.storage.controller;

import com.gemsflare.gemsflare.storage.service.StorageFolderService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/folder")
public class FolderController {

    @Autowired
    private StorageFolderService folderService;

    @Operation(summary = "Delete a folder and all its files")
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteFolder(HttpServletRequest request,
                                          @RequestParam("folder") String folder) {
        return folderService.deleteItemFolder(request, folder);
    }
}