package com.gemsflare.gemsflare.user.service;

import com.gemsflare.gemsflare.address.jpa.BillAddressEntity;
import com.gemsflare.gemsflare.address.jpa.DeliveryAddressEntity;
import com.gemsflare.gemsflare.address.model.AddressDTOs;
import com.gemsflare.gemsflare.address.model.BillAddressDTO;
import com.gemsflare.gemsflare.address.model.DeliveryAddressDTO;
import com.gemsflare.gemsflare.address.repository.BillAddressRepository;
import com.gemsflare.gemsflare.address.repository.DeliveryAddressRepository;
import com.gemsflare.gemsflare.email.EmailService;
import com.gemsflare.gemsflare.permission.service.PermissionService;
import com.gemsflare.gemsflare.security.JwtUtil;
import com.gemsflare.gemsflare.user.jpa.UserEntity;
import com.gemsflare.gemsflare.user.model.LoginResponseDTO;
import com.gemsflare.gemsflare.user.model.TokenInfoDTO;
import com.gemsflare.gemsflare.user.model.UserDTO;
import com.gemsflare.gemsflare.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private DeliveryAddressRepository deliveryAddressRepository;
    @Autowired
    private BillAddressRepository billAddressRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private EmailService emailService;

    public ResponseEntity<?> getTokenInfo(HttpServletRequest request) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing or invalid token");
        }

        token = token.substring(7);

        String validatedToken = jwtUtil.validateAndRenewToken(token);
        if (validatedToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        Claims claims = Jwts.parser().setSigningKey(jwtUtil.getKey()).build().parseClaimsJws(validatedToken).getBody();
        Date expirationDate = claims.getExpiration();
        boolean isValid = expirationDate.after(new Date());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedExpirationDate = dateFormat.format(expirationDate);

        TokenInfoDTO tokenInfoDTO = new TokenInfoDTO(validatedToken, isValid, formattedExpirationDate);
        return ResponseEntity.ok(tokenInfoDTO);
    }

    public ResponseEntity<Object> login(String usernameOrEmail, String password) {
        Optional<UserEntity> optionalUser = userRepository.findByUsername(usernameOrEmail);

        if (optionalUser.isEmpty()) {
            optionalUser = userRepository.findByEmail(usernameOrEmail);
        }

        if (optionalUser.isPresent() && optionalUser.get().getPassword().equals(password)) {
            UserEntity user = optionalUser.get();
            String token = jwtUtil.generateToken(user.getId());

            return ResponseEntity.ok(new LoginResponseDTO(token, user.getId(), user.getUsername(), user.getName(), user.getLastname()));
        } else {
            return ResponseEntity.status(401).body("Invalid username/email or password");
        }
    }

    public ResponseEntity<?> getAllUsers(HttpServletRequest request) {
        if (!permissionService.hasPermission(request, "/getAllUsers")) {
            return ResponseEntity.status(403).body("Access denied: Permission required");
        }

        List<UserDTO> users = userRepository.findAll().stream()
                .map(user -> {
                    AddressDTOs addresses = getUserAddresses(user.getId());

                    return new UserDTO(
                            user.getId(),
                            user.getUsername(),
                            user.getName(),
                            user.getLastname(),
                            user.getRole(),
                            user.getEmail(),
                            user.getTelephone(),
                            addresses.getDeliveryAddress(),
                            addresses.getBillAddress()
                    );
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(users);
    }

    public ResponseEntity<Object> getUserById(UUID id) {
        Optional<UserEntity> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();

            AddressDTOs addresses = getUserAddresses(user.getId());

            UserDTO userDTO = new UserDTO(
                    user.getId(),
                    user.getUsername(),
                    user.getName(),
                    user.getLastname(),
                    user.getRole(),
                    user.getEmail(),
                    user.getTelephone(),
                    addresses.getDeliveryAddress(),
                    addresses.getBillAddress()
            );

            return ResponseEntity.ok(userDTO);
        } else {
            return ResponseEntity.status(404).body("User not found");
        }
    }

    public ResponseEntity<?> getUserByUsername(String username) {
        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();

            AddressDTOs addresses = getUserAddresses(user.getId());

            UserDTO userDTO = new UserDTO(
                    user.getId(),
                    user.getUsername(),
                    user.getName(),
                    user.getLastname(),
                    user.getRole(),
                    user.getEmail(),
                    user.getTelephone(),
                    addresses.getDeliveryAddress(),
                    addresses.getBillAddress()
            );

            return ResponseEntity.ok(userDTO);
        } else {
            return ResponseEntity.status(404).body("User not found");
        }
    }

    public ResponseEntity<?> getUserByFullName(String name, String lastname) {
        Optional<UserEntity> optionalUser = userRepository.findByNameAndLastname(name, lastname);

        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();

            AddressDTOs addresses = getUserAddresses(user.getId());

            UserDTO userDTO = new UserDTO(user.getId(),
                    user.getUsername(),
                    user.getName(),
                    user.getLastname(),
                    user.getRole(),
                    user.getEmail(),
                    user.getTelephone(),
                    addresses.getDeliveryAddress(),
                    addresses.getBillAddress()
            );

            return ResponseEntity.ok(userDTO);
        } else {
            return ResponseEntity.status(404).body("User not found");
        }
    }

    public ResponseEntity<?> addNewUser(String username, String name, String lastName, String email, String telephone, String password) {
        ResponseEntity<?> validationResponse = validateUserInput(username, name, lastName, email, telephone);
        if (!validationResponse.getStatusCode().is2xxSuccessful()) {
            return validationResponse;
        }

        if (password == null || password.length() < 6) {
            return ResponseEntity.status(400).body("Password must be at least 6 characters long");
        }

        if (userRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.status(400).body("Username already exists");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.status(400).body("Email already exists");
        }

        UserEntity newUser = new UserEntity();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setName(name);
        newUser.setLastname(lastName);
        newUser.setEmail(email);
        newUser.setTelephone(telephone);
        newUser.setRole("user");

        newUser = userRepository.save(newUser);
        emailService.sendWelcomeEmail(email, username);
        String token = jwtUtil.generateToken(newUser.getId());

        return ResponseEntity.ok(new LoginResponseDTO(token, newUser.getId(), newUser.getUsername(), newUser.getName(), newUser.getLastname()));
    }

    public ResponseEntity<?> deleteUser(HttpServletRequest request, String password) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Unauthorized: No token provided");
        }

        String token = authorizationHeader.substring(7);
        if (!jwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid token");
        }

        UUID userId = jwtUtil.getUserIdFromToken(token);
        Optional<UserEntity> optionalUser = userRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        UserEntity user = optionalUser.get();

        if (!user.getPassword().equals(password)) {
            return ResponseEntity.status(401).body("Incorrect password");
        }

        userRepository.deleteById(userId);
        emailService.sendDeletedUserEmail(user.getEmail(), user.getUsername());
        return ResponseEntity.ok("User deleted successfully");
    }

    public ResponseEntity<?> deleteUserByAdmin(HttpServletRequest request, UUID id, String username) {
        if (!permissionService.hasPermission(request, "/deleteUserByAdmin")) {
            return ResponseEntity.status(403).body("Access denied: Permission required");
        }

        Optional<UserEntity> userToDelete = Optional.empty();

        if (id != null) {
            userToDelete = userRepository.findById(id);
        } else if (username != null) {
            userToDelete = userRepository.findByUsername(username);
        }

        if (userToDelete.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        userRepository.delete(userToDelete.get());
        emailService.sendDeletedUserEmail(userToDelete.get().getEmail(), userToDelete.get().getUsername());
        return ResponseEntity.ok("User deleted successfully by admin");
    }

    public ResponseEntity<?> changeMyPassword(HttpServletRequest request, String oldPassword, String newPassword) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Unauthorized: No token provided");
        }

        String token = authorizationHeader.substring(7);
        if (!jwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid token");
        }

        UUID userId = jwtUtil.getUserIdFromToken(token);
        Optional<UserEntity> optionalUser = userRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        UserEntity user = optionalUser.get();

        if (!oldPassword.equals(user.getPassword())) {
            return ResponseEntity.status(400).body("Old password is incorrect");
        }

        if (newPassword.length() < 6) {
            return ResponseEntity.status(400).body("New password must be at least 6 characters");
        }

        user.setPassword(newPassword);
        userRepository.save(user);

        return ResponseEntity.ok("Password changed successfully");
    }

    public ResponseEntity<?> changePasswordByAdmin(HttpServletRequest request, UUID userId, String username, String newPassword) {
        if (!permissionService.hasPermission(request, "/changePasswordByAdmin")) {
            return ResponseEntity.status(403).body("Access denied: Permission required");
        }

        Optional<UserEntity> userToUpdate = Optional.empty();

        if (userId != null) {
            userToUpdate = userRepository.findById(userId);
        } else if (username != null) {
            userToUpdate = userRepository.findByUsername(username);
        }

        if (userToUpdate.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        UserEntity user = userToUpdate.get();

        if (newPassword.length() < 6) {
            return ResponseEntity.status(400).body("New password must be at least 6 characters");
        }

        user.setPassword(newPassword);
        userRepository.save(user);

        return ResponseEntity.ok("Password changed successfully by admin");
    }

    public ResponseEntity<?> editMyProfile(HttpServletRequest request, String username, String name, String lastName, String email, String telephone) {
        UUID userId = getUserIdFromRequest(request);
        if (userId == null) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid token");
        }

        Optional<UserEntity> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        UserEntity user = userOptional.get();

        ResponseEntity<?> validationResponse = validateUserInput(username, name, lastName, email, telephone);
        if (!validationResponse.getStatusCode().is2xxSuccessful()) {
            return validationResponse;
        }

        if (username != null && !username.equals(user.getUsername()) && userRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.status(400).body("Username already exists");
        }
        if (email != null && !email.equals(user.getEmail()) && userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.status(400).body("Email already exists");
        }

        if (username != null) user.setUsername(username);
        if (name != null) user.setName(name);
        if (lastName != null) user.setLastname(lastName);
        if (email != null) user.setEmail(email);
        if (telephone != null) user.setTelephone(telephone);

        userRepository.save(user);
        emailService.sendEditedProfileEmail(user.getEmail(), user.getUsername());
        return ResponseEntity.ok("Profile updated successfully");
    }

    public ResponseEntity<?> editUserProfileByAdmin(HttpServletRequest request, UUID id, String username, String newUsername, String name, String lastName, String email, String telephone) {
        if (!permissionService.hasPermission(request, "/editUserProfileByAdmin")) {
            return ResponseEntity.status(403).body("Access denied: Permission required");
        }

        Optional<UserEntity> userToUpdate = Optional.empty();
        if (id != null) {
            userToUpdate = userRepository.findById(id);
        } else if (username != null) {
            userToUpdate = userRepository.findByUsername(username);
        }

        if (userToUpdate.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        UserEntity user = userToUpdate.get();

        ResponseEntity<?> validationResponse = validateUserInput(newUsername, name, lastName, email, telephone);
        if (!validationResponse.getStatusCode().is2xxSuccessful()) {
            return validationResponse;
        }

        if (newUsername != null && !newUsername.equals(user.getUsername()) && userRepository.findByUsername(newUsername).isPresent()) {
            return ResponseEntity.status(400).body("Username already exists");
        }
        if (email != null && !email.equals(user.getEmail()) && userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.status(400).body("Email already exists");
        }

        if (newUsername != null) user.setUsername(newUsername);
        if (name != null) user.setName(name);
        if (lastName != null) user.setLastname(lastName);
        if (email != null) user.setEmail(email);
        if (telephone != null) user.setTelephone(telephone);

        userRepository.save(user);
        emailService.sendEditedProfileEmail(user.getEmail(), user.getUsername());
        return ResponseEntity.ok("User profile updated successfully by admin");
    }

    public List<UserDTO> getUserInfoList(List<UUID> userIds) {
        return userIds.stream()
                .map(userId -> userRepository.findById(userId)
                        .map(user -> new UserDTO(user.getId(), user.getUsername()))
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();
    }

    public UUID getUserIdByUsernameOrId(UUID userId, String username) {
        if (userId != null) {
            return userId;
        }
        if (username != null) {
            Optional<UserEntity> user = userRepository.findByUsername(username);
            return user.map(UserEntity::getId).orElse(null);
        }
        return null;
    }

    public UUID getUserIdFromRequest(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authorizationHeader.substring(7);
        if (!jwtUtil.isTokenValid(token)) {
            return null;
        }
        return jwtUtil.getUserIdFromToken(token);
    }

    public AddressDTOs getUserAddresses(UUID userId) {
        DeliveryAddressEntity deliveryAddress = deliveryAddressRepository.findByUserid(userId);
        BillAddressEntity billAddress = billAddressRepository.findByUserid(userId);

        DeliveryAddressDTO deliveryAddressDTO = (deliveryAddress != null)
                ? new DeliveryAddressDTO(
                deliveryAddress.getName(),
                deliveryAddress.getLastname(),
                deliveryAddress.getStreet(),
                deliveryAddress.getHousenumber(),
                deliveryAddress.getZipcode(),
                deliveryAddress.getCounty(),
                deliveryAddress.getCountry())
                : null;

        BillAddressDTO billAddressDTO = (billAddress != null)
                ? new BillAddressDTO(
                billAddress.getName(),
                billAddress.getLastname(),
                billAddress.getStreet(),
                billAddress.getHousenumber(),
                billAddress.getZipcode(),
                billAddress.getCounty(),
                billAddress.getCountry())
                : null;

        return new AddressDTOs(deliveryAddressDTO, billAddressDTO);
    }

    private ResponseEntity<?> validateUserInput(String username, String name, String lastName, String email, String telephone) {
        String namePattern = "^[A-Za-z]+$";
        String usernamePattern = "^[A-Za-z0-9.]+$";
        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        String telephonePattern = "^\\+?[0-9]+$";

        if (name == null || !name.matches(namePattern)) {
            return ResponseEntity.status(400).body("Invalid name: Only letters are allowed");
        }
        if (lastName == null || !lastName.matches(namePattern)) {
            return ResponseEntity.status(400).body("Invalid last name: Only letters are allowed");
        }
        if (username == null || !username.matches(usernamePattern)) {
            return ResponseEntity.status(400).body("Invalid username: Only letters, numbers, and dots (.) are allowed");
        }
        if (email == null || !Pattern.compile(emailPattern).matcher(email).matches()) {
            return ResponseEntity.status(400).body("Invalid email format");
        }
        if (telephone != null && !telephone.matches(telephonePattern)) {
            return ResponseEntity.status(400).body("Invalid telephone: Only numbers and an optional leading '+' are allowed");
        }

        return ResponseEntity.ok().build();
    }

}
