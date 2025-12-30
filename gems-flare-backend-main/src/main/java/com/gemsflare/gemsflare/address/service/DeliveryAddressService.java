package com.gemsflare.gemsflare.address.service;

import com.gemsflare.gemsflare.address.jpa.DeliveryAddressEntity;
import com.gemsflare.gemsflare.address.model.DeliveryAddressDTO;
import com.gemsflare.gemsflare.address.repository.DeliveryAddressRepository;
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
public class DeliveryAddressService {

    @Autowired
    private DeliveryAddressRepository deliveryAddressRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<?> addMyDeliveryAddress(HttpServletRequest request, DeliveryAddressEntity deliveryAddressEntity) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Unauthorized: No token provided");
        }

        String token = authorizationHeader.substring(7);
        if (!jwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid token");
        }

        UUID userId = jwtUtil.getUserIdFromToken(token);

        if (deliveryAddressRepository.findByUserid(userId) != null) {
            return ResponseEntity.status(400).body("User already has a delivery address");
        }

        deliveryAddressEntity.setUserid(userId);
        deliveryAddressEntity.setId(null);
        deliveryAddressRepository.save(deliveryAddressEntity);

        return ResponseEntity.ok("Delivery address added successfully");
    }

    public ResponseEntity<?> removeMyDeliveryAddress(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Unauthorized: No token provided");
        }

        String token = authorizationHeader.substring(7);
        if (!jwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid token");
        }

        UUID userId = jwtUtil.getUserIdFromToken(token);

        DeliveryAddressEntity deliveryAddress = deliveryAddressRepository.findByUserid(userId);
        if (deliveryAddress == null) {
            return ResponseEntity.status(404).body("No delivery address found for the user");
        }

        deliveryAddressRepository.delete(deliveryAddress);
        return ResponseEntity.ok("Delivery address removed successfully");
    }

    public ResponseEntity<?> editMyDeliveryAddress(HttpServletRequest request, DeliveryAddressEntity updatedAddress) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Unauthorized: No token provided");
        }

        String token = authorizationHeader.substring(7);
        if (!jwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid token");
        }

        UUID userId = jwtUtil.getUserIdFromToken(token);

        DeliveryAddressEntity existingAddress = deliveryAddressRepository.findByUserid(userId);
        if (existingAddress == null) {
            return ResponseEntity.status(404).body("No delivery address found for the user");
        }

        existingAddress.setName(updatedAddress.getName());
        existingAddress.setLastname(updatedAddress.getLastname());
        existingAddress.setStreet(updatedAddress.getStreet());
        existingAddress.setHousenumber(updatedAddress.getHousenumber());
        existingAddress.setZipcode(updatedAddress.getZipcode());
        existingAddress.setCounty(updatedAddress.getCounty());
        existingAddress.setCountry(updatedAddress.getCountry());

        deliveryAddressRepository.save(existingAddress);
        return ResponseEntity.ok("Delivery address updated successfully");
    }

    public ResponseEntity<?> getMyDeliveryAddress(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Unauthorized: No token provided");
        }

        String token = authorizationHeader.substring(7);
        if (!jwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid token");
        }

        UUID userId = jwtUtil.getUserIdFromToken(token);

        DeliveryAddressEntity existingAddress = deliveryAddressRepository.findByUserid(userId);
        if (existingAddress == null) {
            return ResponseEntity.status(404).body("No delivery address found for the user");
        }

        DeliveryAddressDTO addressDTO = new DeliveryAddressDTO(
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

    public DeliveryAddressDTO getMyDeliveryAddressDTO(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return null;
        }

        String token = authorizationHeader.substring(7);
        if (!jwtUtil.isTokenValid(token)) {
            return null;
        }

        UUID userId = jwtUtil.getUserIdFromToken(token);

        DeliveryAddressEntity existingAddress = deliveryAddressRepository.findByUserid(userId);
        if (existingAddress == null) {
            return null;
        }

        DeliveryAddressDTO addressDTO = new DeliveryAddressDTO(
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

    public ResponseEntity<?> getAllDeliveryAddresses(HttpServletRequest request) {
        if (!permissionService.hasPermission(request, "/getAllDeliveryAddresses")) {
            return ResponseEntity.status(403).body("Access denied: Permission required");
        }

        List<DeliveryAddressDTO> addresses = deliveryAddressRepository.findAll().stream()
                .map(address -> new DeliveryAddressDTO(
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

    public ResponseEntity<?> addDeliveryAddressToUserByAdmin(HttpServletRequest request, UUID userId, String username, DeliveryAddressDTO addressDTO) {
        if (!permissionService.hasPermission(request, "/addDeliveryAddressToUserByAdmin")) {
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

        if (deliveryAddressRepository.findByUserid(user.getId()) != null) {
            return ResponseEntity.status(400).body("User already has a delivery address");
        }

        DeliveryAddressEntity newAddress = new DeliveryAddressEntity();
        newAddress.setUserid(user.getId());
        newAddress.setName(addressDTO.getName());
        newAddress.setLastname(addressDTO.getLastname());
        newAddress.setStreet(addressDTO.getStreet());
        newAddress.setHousenumber(addressDTO.getHousenumber());
        newAddress.setZipcode(addressDTO.getZipcode());
        newAddress.setCounty(addressDTO.getCounty());
        newAddress.setCountry(addressDTO.getCountry());

        deliveryAddressRepository.save(newAddress);

        deliveryAddressRepository.save(newAddress);
        return ResponseEntity.ok("Delivery address added successfully for user: " + user.getUsername());
    }

    public ResponseEntity<?> removeDeliveryAddressFromUserByAdmin(HttpServletRequest request, UUID userId, String username) {
        if (!permissionService.hasPermission(request, "/removeDeliveryAddressFromUserByAdmin")) {
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

        DeliveryAddressEntity existingAddress = deliveryAddressRepository.findByUserid(user.getId());
        if (existingAddress == null) {
            return ResponseEntity.status(400).body("User does not have a delivery address to remove");
        }

        deliveryAddressRepository.delete(existingAddress);
        return ResponseEntity.ok("Delivery address removed successfully for user: " + user.getUsername());
    }

}
