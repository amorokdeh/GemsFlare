package com.gemsflare.gemsflare.permission.controller;

import com.gemsflare.gemsflare.permission.service.PermissionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @PostMapping("/addPermissionRoute")
    public ResponseEntity<?> addPermissionRoute(HttpServletRequest request,
                                                @RequestParam String route,
                                                @RequestParam(required = false) UUID adminUserId,
                                                @RequestParam(required = false) String adminUsername) {
        return permissionService.addPermissionRoute(request, route, adminUserId, adminUsername);
    }

    @PostMapping("/addPermissionToUser")
    public ResponseEntity<?> addPermissionToUser(HttpServletRequest request,
                                                 @RequestParam String route,
                                                 @RequestParam(required = false) UUID userId,
                                                 @RequestParam(required = false) String username) {
        return permissionService.addPermissionToUser(request, route, userId, username);
    }

    @GetMapping("/getMyPermissions")
    public ResponseEntity<?> getMyPermissions(HttpServletRequest request) {
        return permissionService.getMyPermissions(request);
    }

    @GetMapping("/getUserPermissions")
    public ResponseEntity<?> getUserPermissions(HttpServletRequest request,
                                                @RequestParam(required = false) UUID userId,
                                                @RequestParam(required = false) String username) {
        return permissionService.getUserPermissions(request, userId, username);
    }

    @GetMapping("/getUserItemsPermissions")
    public ResponseEntity<?> getUserItemsPermissions(HttpServletRequest request,
                                                @RequestParam(required = false) UUID userId,
                                                @RequestParam(required = false) String username) {
        return permissionService.getUserItemsPermissions(request, userId, username);
    }

    @GetMapping("/getAllPermissions")
    public ResponseEntity<?> getAllPermissions(HttpServletRequest request) {
        return permissionService.getAllPermissions(request);
    }

    @GetMapping("/checkPermission")
    public ResponseEntity<?> checkPermission(HttpServletRequest request, @RequestParam String route) {
        return permissionService.checkPermission(request, route);
    }

    @GetMapping("/checkAdminPermission")
    public ResponseEntity<?> checkAdminPermission(HttpServletRequest request, @RequestParam String route) {
        return permissionService.checkAdminPermission(request, route);
    }

    @DeleteMapping("/removePermissionFromUser")
    public ResponseEntity<?> removePermissionFromUser(HttpServletRequest request,
                                                      @RequestParam String route,
                                                      @RequestParam(required = false) UUID userId,
                                                      @RequestParam(required = false) String username) {
        return permissionService.removePermissionFromUser(request, route, userId, username);
    }

    @DeleteMapping("/deletePermission")
    public ResponseEntity<?> deletePermission(HttpServletRequest request,
                                              @RequestParam String route,
                                              @RequestParam String password) {
        return permissionService.deletePermission(request, route, password);
    }
}

