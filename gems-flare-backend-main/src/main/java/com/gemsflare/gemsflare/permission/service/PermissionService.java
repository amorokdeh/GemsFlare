package com.gemsflare.gemsflare.permission.service;

import com.gemsflare.gemsflare.permission.model.PermissionDTO;
import com.gemsflare.gemsflare.security.JwtUtil;
import com.gemsflare.gemsflare.permission.jpa.PermissionEntity;
import com.gemsflare.gemsflare.user.jpa.UserEntity;
import com.gemsflare.gemsflare.permission.repository.PermissionRepository;
import com.gemsflare.gemsflare.user.repository.UserRepository;
import com.gemsflare.gemsflare.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PermissionService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private final UserService userService;

    public PermissionService(@Lazy UserService userService) {
        this.userService = userService;
    }

    public ResponseEntity<?> addPermissionItem(HttpServletRequest request, String itemNumber) {
        UUID requesterId = userService.getUserIdFromRequest(request);

        UUID finalUserId = userService.getUserIdByUsernameOrId(requesterId, "");
        if (finalUserId == null) {
            return ResponseEntity.status(404).body("Error: User not found");
        }

        PermissionEntity newPermission = new PermissionEntity();
        newPermission.setRoute("/item/" + itemNumber);
        newPermission.setAdmins(List.of(finalUserId));
        newPermission.setUsers(List.of(finalUserId));

        permissionRepository.save(newPermission);
        return ResponseEntity.ok("Success: Permission item created and admin assigned");
    }

    public ResponseEntity<?> addPermissionRoute(HttpServletRequest request, String route, UUID adminUserId, String username) {
        UUID requesterId = userService.getUserIdFromRequest(request);
        if (requesterId == null || !isAdmin(requesterId)) {
            return ResponseEntity.status(403).body("Access denied: Admin role required");
        }

        if (permissionRepository.findByRoute(route).isPresent()) {
            return ResponseEntity.status(400).body("Error: Route already exists in the permission database");
        }

        UUID finalAdminUserId = userService.getUserIdByUsernameOrId(adminUserId, username);
        if (finalAdminUserId == null) {
            return ResponseEntity.status(404).body("Error: Admin user not found");
        }

        PermissionEntity newPermission = new PermissionEntity();
        newPermission.setRoute(route);
        newPermission.setUsers(List.of(finalAdminUserId));
        newPermission.setAdmins(List.of(finalAdminUserId));

        permissionRepository.save(newPermission);
        return ResponseEntity.ok("Success: Permission route created and admin assigned");
    }

    public ResponseEntity<?> addPermissionToUser(HttpServletRequest request, String route, UUID userId, String username) {
        UUID requesterId = userService.getUserIdFromRequest(request);
        if (requesterId == null || !hasAdminPermission(requesterId, route)) {
            return ResponseEntity.status(403).body("Access denied: Only admins or permission admins can modify this permission");
        }

        Optional<PermissionEntity> optionalPermission = permissionRepository.findByRoute(route);
        if (optionalPermission.isEmpty()) {
            return ResponseEntity.status(404).body("Error: Permission not found");
        }

        UUID finalUserId = userService.getUserIdByUsernameOrId(userId, username);
        if (finalUserId == null) {
            return ResponseEntity.status(404).body("Error: User not found");
        }

        PermissionEntity permission = optionalPermission.get();
        List<UUID> users = permission.getUsers();

        if (!users.contains(finalUserId)) {
            users.add(finalUserId);
            permission.setUsers(users);
            permissionRepository.save(permission);
            return ResponseEntity.ok("Success: User added to the permission");
        } else {
            return ResponseEntity.status(400).body("Error: User already has permission");
        }
    }

    public ResponseEntity<?> getMyPermissions(HttpServletRequest request) {
        UUID userId = userService.getUserIdFromRequest(request);
        if (userId == null) {
            return ResponseEntity.status(401).body("Error: Unauthorized");
        }

        List<String> accessibleRoutes = permissionRepository.findAll().stream()
                .filter(permission -> permission.getUsers().contains(userId) || permission.getAdmins().contains(userId))
                .map(permission -> {
                    boolean isAdmin = permission.getAdmins().contains(userId);
                    String role = isAdmin ? "Admin" : "User";
                    return permission.getRoute() + ": " + role;
                })
                .toList();

        return ResponseEntity.ok(accessibleRoutes);
    }

    public ResponseEntity<?> getUserPermissions(HttpServletRequest request, UUID userId, String username) {
        UUID requesterId = userService.getUserIdFromRequest(request);
        if (requesterId == null || !hasPermission(request, "/getUserPermissions")) {
            return ResponseEntity.status(403).body("Access denied: Only admins or permission admins can view user permissions");
        }

        UUID targetUserId = userService.getUserIdByUsernameOrId(userId, username);
        if (targetUserId == null) {
            return ResponseEntity.status(404).body("Error: User not found");
        }

        List<String> accessibleRoutes = permissionRepository.findAll().stream()
                .filter(permission -> permission.getUsers().contains(targetUserId) || permission.getAdmins().contains(targetUserId))
                .map(permission -> {
                    boolean isAdmin = permission.getAdmins().contains(targetUserId);
                    String role = isAdmin ? "Admin" : "User";
                    return permission.getRoute() + ": " + role;
                })
                .toList();

        return ResponseEntity.ok(accessibleRoutes);
    }

    public ResponseEntity<?> getUserItemsPermissions(HttpServletRequest request, UUID userId, String username) {
        UUID requesterId = userService.getUserIdFromRequest(request);
        if (requesterId == null || !hasPermission(request, "/addItem")) {
            return ResponseEntity.status(403).body("Access denied: Only admins or permission admins can view user permissions");
        }

        UUID targetUserId = userService.getUserIdByUsernameOrId(userId, username);
        if (targetUserId == null) {
            return ResponseEntity.status(404).body("Error: User not found");
        }

        List<String> itemNumbers = permissionRepository.findAll().stream()
                .filter(permission ->
                        (permission.getUsers().contains(targetUserId) || permission.getAdmins().contains(targetUserId)) &&
                                permission.getRoute().startsWith("/item/")
                )
                .map(permission -> permission.getRoute().replace("/item/", ""))
                .toList();

        return ResponseEntity.ok(itemNumbers);
    }

    public ResponseEntity<?> getAllPermissions(HttpServletRequest request) {
        UUID requesterId = userService.getUserIdFromRequest(request);
        if (requesterId == null || !hasPermission(request, "/getAllPermissions")) {
            return ResponseEntity.status(403).body("Access denied: Only admins or permission admins can view user permissions");
        }

        List<PermissionDTO> allPermissions = permissionRepository.findAll().stream()
                .map(permission -> new PermissionDTO(
                        permission.getRoute(),
                        userService.getUserInfoList(permission.getAdmins()),
                        userService.getUserInfoList(permission.getUsers())
                ))
                .toList();

        return ResponseEntity.ok(allPermissions);
    }

    public ResponseEntity<?> removePermissionFromUser(HttpServletRequest request, String route, UUID userId, String username) {
        UUID requesterId = userService.getUserIdFromRequest(request);
        if (requesterId == null || !hasAdminPermission(requesterId, route)) {
            return ResponseEntity.status(403).body("Access denied: Only admins or permission admins can modify this permission");
        }

        Optional<PermissionEntity> optionalPermission = permissionRepository.findByRoute(route);
        if (optionalPermission.isEmpty()) {
            return ResponseEntity.status(404).body("Error: Permission not found");
        }

        UUID finalUserId = userService.getUserIdByUsernameOrId(userId, username);
        if (finalUserId == null) {
            return ResponseEntity.status(404).body("Error: User not found");
        }

        PermissionEntity permission = optionalPermission.get();
        List<UUID> users = permission.getUsers();

        if (!users.contains(finalUserId)) {
            return ResponseEntity.status(400).body("Error: User does not have this permission");
        }

        users.remove(finalUserId);
        permission.setUsers(users);
        permissionRepository.save(permission);

        return ResponseEntity.ok("Success: User removed from the permission");
    }

    public ResponseEntity<?> deletePermission(HttpServletRequest request, String route, String password) {
        UUID requesterId = userService.getUserIdFromRequest(request);
        if (requesterId == null || !hasAdminPermission(requesterId, route)) {
            return ResponseEntity.status(403).body("Access denied: Only admins or permission admins can delete this permission");
        }

        Optional<UserEntity> optionalUser = userRepository.findById(requesterId);
        if (optionalUser.isEmpty() || !optionalUser.get().getPassword().equals(password)) {
            return ResponseEntity.status(401).body("Error: Incorrect password");
        }

        Optional<PermissionEntity> optionalPermission = permissionRepository.findByRoute(route);
        if (optionalPermission.isEmpty()) {
            return ResponseEntity.status(404).body("Error: Permission not found");
        }

        permissionRepository.delete(optionalPermission.get());
        return ResponseEntity.ok("Success: Permission deleted");
    }

    public ResponseEntity<?> checkPermission(HttpServletRequest request, String route) {
        if (hasPermission(request, route)) {
            return ResponseEntity.ok("User has Permission");
        } else {
            return ResponseEntity.status(404).body("Permission not found");
        }
    }

    public ResponseEntity<?> checkAdminPermission(HttpServletRequest request, String route) {
        UUID requesterId = userService.getUserIdFromRequest(request);
        if (hasAdminPermission(requesterId, route)) {
            return ResponseEntity.ok("User has Admin Permission");
        } else {
            return ResponseEntity.status(404).body("Permission not found");
        }
    }

    private boolean isAdmin(UUID userId) {
        Optional<UserEntity> user = userRepository.findById(userId);
        return user.isPresent() && "admin".equalsIgnoreCase(user.get().getRole());
    }

    private boolean hasAdminPermission(UUID userId, String route) {
        if (isAdmin(userId)) return true;

        Optional<PermissionEntity> permission = permissionRepository.findByRoute(route);
        return permission.map(p -> p.getAdmins().contains(userId)).orElse(false);
    }

    public boolean hasPermission(HttpServletRequest request, String route) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return false;
        }

        String token = authorizationHeader.substring(7);
        if (!jwtUtil.isTokenValid(token)) {
            return false;
        }

        UUID userId = jwtUtil.getUserIdFromToken(token);
        Optional<UserEntity> optionalUser = userRepository.findById(userId);

        if (optionalUser.isPresent() && "admin".equalsIgnoreCase(optionalUser.get().getRole())) {
            return true;
        }

        Optional<PermissionEntity> permission = permissionRepository.findByRoute(route);

        return permission.map(p -> p.getUsers().contains(userId)).orElse(false);
    }

}
