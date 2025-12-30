package com.gemsflare.gemsflare.address.service;

import com.gemsflare.gemsflare.address.jpa.BillAddressEntity;
import com.gemsflare.gemsflare.address.model.BillAddressDTO;
import com.gemsflare.gemsflare.address.repository.BillAddressRepository;
import com.gemsflare.gemsflare.permission.service.PermissionService;
import com.gemsflare.gemsflare.security.JwtUtil;
import com.gemsflare.gemsflare.user.jpa.UserEntity;
import com.gemsflare.gemsflare.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service

public class BillAddressService {

    @Autowired
    private BillAddressRepository billAddressRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<?> addMyBillAddress(HttpServletRequest request, BillAddressEntity billAddressEntity) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Unauthorized: No token provided");
        }

        String token = authorizationHeader.substring(7);
        if (!jwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid token");
        }

        UUID userId = jwtUtil.getUserIdFromToken(token);

        if (billAddressRepository.findByUserid(userId) != null) {
            return ResponseEntity.status(400).body("User already has a bill address");
        }

        billAddressEntity.setUserid(userId);
        billAddressEntity.setId(null);
        billAddressRepository.save(billAddressEntity);

        return ResponseEntity.ok("Bill address added successfully");
    }

    public ResponseEntity<?> removeMyBillAddress(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Unauthorized: No token provided");
        }

        String token = authorizationHeader.substring(7);
        if (!jwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid token");
        }

        UUID userId = jwtUtil.getUserIdFromToken(token);

        BillAddressEntity billAddress = billAddressRepository.findByUserid(userId);
        if (billAddress == null) {
            return ResponseEntity.status(404).body("No bill address found for the user");
        }

        billAddressRepository.delete(billAddress);
        return ResponseEntity.ok("Bill address removed successfully");
    }

    public ResponseEntity<?> editMyBillAddress(HttpServletRequest request, BillAddressEntity updatedAddress) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Unauthorized: No token provided");
        }

        String token = authorizationHeader.substring(7);
        if (!jwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid token");
        }

        UUID userId = jwtUtil.getUserIdFromToken(token);

        BillAddressEntity existingAddress = billAddressRepository.findByUserid(userId);
        if (existingAddress == null) {
            return ResponseEntity.status(404).body("No bill address found for the user");
        }

        existingAddress.setName(updatedAddress.getName());
        existingAddress.setLastname(updatedAddress.getLastname());
        existingAddress.setStreet(updatedAddress.getStreet());
        existingAddress.setHousenumber(updatedAddress.getHousenumber());
        existingAddress.setZipcode(updatedAddress.getZipcode());
        existingAddress.setCounty(updatedAddress.getCounty());
        existingAddress.setCountry(updatedAddress.getCountry());

        billAddressRepository.save(existingAddress);
        return ResponseEntity.ok("Bill address updated successfully");
    }

    public ResponseEntity<?> getMyBillAddress(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Unauthorized: No token provided");
        }

        String token = authorizationHeader.substring(7);
        if (!jwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid token");
        }

        UUID userId = jwtUtil.getUserIdFromToken(token);

        BillAddressEntity existingAddress = billAddressRepository.findByUserid(userId);
        if (existingAddress == null) {
            return ResponseEntity.status(404).body("No bill address found for the user");
        }

        BillAddressDTO addressDTO = new BillAddressDTO(
                existingAddress.getName(),
                existingAddress.getLastname(),
                existingAddress.getStreet(),
                existingAddress.getHousenumber(),
                existingAddress.getZipcode(),
                existingAddress.getCounty(),
                existingAddress.getCountry()
        );

        return ResponseEntity.ok(addressDTO);
    }

    public BillAddressDTO getMyBillAddressDTO(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return null;
        }

        String token = authorizationHeader.substring(7);
        if (!jwtUtil.isTokenValid(token)) {
            return null;
        }

        UUID userId = jwtUtil.getUserIdFromToken(token);

        BillAddressEntity existingAddress = billAddressRepository.findByUserid(userId);
        if (existingAddress == null) {
            return null;
        }

        BillAddressDTO addressDTO = new BillAddressDTO(
                existingAddress.getName(),
                existingAddress.getLastname(),
                existingAddress.getStreet(),
                existingAddress.getHousenumber(),
                existingAddress.getZipcode(),
                existingAddress.getCounty(),
                existingAddress.getCountry()
        );

        return addressDTO;
    }

    public ResponseEntity<?> getAllBillAddresses(HttpServletRequest request) {
        if (!permissionService.hasPermission(request, "/getAllBillAddresses")) {
            return ResponseEntity.status(403).body("Access denied: Permission required");
        }

        List<BillAddressDTO> addresses = billAddressRepository.findAll().stream()
                .map(address -> new BillAddressDTO(
                        address.getName(),
                        address.getLastname(),
                        address.getStreet(),
                        address.getHousenumber(),
                        address.getZipcode(),
                        address.getCounty(),
                        address.getCountry()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(addresses);
    }

    public ResponseEntity<?> addBillAddressToUserByAdmin(HttpServletRequest request, UUID userId, String username, BillAddressDTO addressDTO) {
        if (!permissionService.hasPermission(request, "/addBillAddressToUserByAdmin")) {
            return ResponseEntity.status(403).body("Access denied: Permission required");
        }

        Optional<UserEntity> optionalUser = Optional.empty();

        if (userId != null) {
            optionalUser = userRepository.findById(userId);
        } else if (username != null) {
            optionalUser = userRepository.findByUsername(username);
        }

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        UserEntity user = optionalUser.get();

        if (billAddressRepository.findByUserid(user.getId()) != null) {
            return ResponseEntity.status(400).body("User already has a bill address");
        }

        BillAddressEntity newAddress = new BillAddressEntity();
        newAddress.setUserid(user.getId());
        newAddress.setName(addressDTO.getName());
        newAddress.setLastname(addressDTO.getLastname());
        newAddress.setStreet(addressDTO.getStreet());
        newAddress.setHousenumber(addressDTO.getHousenumber());
        newAddress.setZipcode(addressDTO.getZipcode());
        newAddress.setCounty(addressDTO.getCounty());
        newAddress.setCountry(addressDTO.getCountry());

        billAddressRepository.save(newAddress);

        billAddressRepository.save(newAddress);
        return ResponseEntity.ok("Bill address added successfully for user: " + user.getUsername());
    }

    public ResponseEntity<?> removeBillAddressFromUserByAdmin(HttpServletRequest request, UUID userId, String username) {
        if (!permissionService.hasPermission(request, "/removeBillAddressFromUserByAdmin")) {
            return ResponseEntity.status(403).body("Access denied: Permission required");
        }

        Optional<UserEntity> optionalUser = Optional.empty();

        if (userId != null) {
            optionalUser = userRepository.findById(userId);
        } else if (username != null) {
            optionalUser = userRepository.findByUsername(username);
        }

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        UserEntity user = optionalUser.get();

        BillAddressEntity existingAddress = billAddressRepository.findByUserid(user.getId());
        if (existingAddress == null) {
            return ResponseEntity.status(400).body("User does not have a bill address to remove");
        }

        billAddressRepository.delete(existingAddress);
        return ResponseEntity.ok("Bill address removed successfully for user: " + user.getUsername());
    }

}
