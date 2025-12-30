package com.gemsflare.gemsflare.user.controller;

import com.gemsflare.gemsflare.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/checkToken")
    public ResponseEntity<?> getTokenInfo(HttpServletRequest request) {
        return userService.getTokenInfo(request);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String usernameOrEmail, @RequestParam String password) {
        return userService.login(usernameOrEmail, password);
    }

    @GetMapping("/getAllUsers")
    public ResponseEntity<?> getAllUsers(HttpServletRequest request) {
        return userService.getAllUsers(request);
    }

    @GetMapping("/getUserById/{id}")
    public ResponseEntity<?> getUserById(@PathVariable UUID id) {
        return userService.getUserById(id);
    }

    @GetMapping("/getUserByUsername")
    public ResponseEntity<?> getUserByUsername(@RequestParam String username) {
        return userService.getUserByUsername(username);
    }

    @GetMapping("/getUserByName")
    public ResponseEntity<?> getUserByFullName(@RequestParam String name,
                                               @RequestParam(required = false) String lastname) {
        return userService.getUserByFullName(name, lastname);
    }

    @PostMapping("/addNewUser")
    public ResponseEntity<?> addNewUser(
            @RequestParam String username,
            @RequestParam String name,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam(required = false) String telephone,
            @RequestParam String password) {
        return userService.addNewUser(username, name, lastName, email, telephone, password);
    }

    @DeleteMapping("/deleteMyUser")
    public ResponseEntity<?> deleteUser(HttpServletRequest request, @RequestParam String password) {
        return userService.deleteUser(request, password);
    }

    @DeleteMapping("/deleteUserByAdmin")
    public ResponseEntity<?> deleteUserByAdmin(HttpServletRequest request,
                                               @RequestParam(required = false) UUID id,
                                               @RequestParam(required = false) String username) {
        return userService.deleteUserByAdmin(request, id, username);
    }

    @PutMapping("/changeMyPassword")
    public ResponseEntity<?> changeMyPassword(
            HttpServletRequest request,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        return userService.changeMyPassword(request, oldPassword, newPassword);
    }

    @PutMapping("/changePasswordByAdmin")
    public ResponseEntity<?> changePasswordByAdmin(
            HttpServletRequest request,
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) String username,
            @RequestParam String newPassword) {
        return userService.changePasswordByAdmin(request, userId, username, newPassword);
    }

    @PutMapping("/editMyProfile")
    public ResponseEntity<?> editMyProfile(
            HttpServletRequest request,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String telephone) {
        return userService.editMyProfile(request, username, name, lastName, email, telephone);
    }

    @PutMapping("/editUserProfileByAdmin")
    public ResponseEntity<?> editUserProfileByAdmin(
            HttpServletRequest request,
            @RequestParam(required = false) UUID id,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String newUsername,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String telephone) {
        return userService.editUserProfileByAdmin(request, id, username, newUsername, name, lastName, email, telephone);
    }
}
