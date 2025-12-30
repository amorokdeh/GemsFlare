package com.gemsflare.gemsflare.checkout.service;

import com.gemsflare.gemsflare.checkout.jpa.CheckoutEntity;
import com.gemsflare.gemsflare.checkout.model.CheckoutDTO;
import com.gemsflare.gemsflare.checkout.repository.CheckoutRepository;
import com.gemsflare.gemsflare.item.model.ItemDTO;
import com.gemsflare.gemsflare.item.service.ItemService;
import com.gemsflare.gemsflare.permission.service.PermissionService;
import com.gemsflare.gemsflare.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CheckoutService {
    @Autowired
    private ItemService itemService;
    @Autowired
    private CheckoutRepository checkoutRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PermissionService permissionService;

    public ResponseEntity<?> getAllCheckout(HttpServletRequest request, int page, int size) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Unauthorized: No token provided");
        }

        String token = authorizationHeader.substring(7);
        if (!jwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid token");
        }

        if (!permissionService.hasPermission(request, "/checkout")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Permission required");
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<CheckoutEntity> CheckoutPage = checkoutRepository.findAll(pageable);

        if (CheckoutPage.isEmpty()) {
            return ResponseEntity.status(404).body("No Checkout process found");
        }

        return ResponseEntity.ok(CheckoutPage);
    }

    public ResponseEntity<?> getCheckoutByNumber(String checkoutNumber) {
        Optional<CheckoutEntity> checkoutOpt = checkoutRepository.findByNumber(checkoutNumber);
        if (checkoutOpt.isPresent()) {
            return ResponseEntity.ok(checkoutOpt.get());
        } else {
            return ResponseEntity.status(404).body("Checkout not found with number: " + checkoutNumber);
        }
    }

    public ResponseEntity<?> addCheckout(HttpServletRequest request, CheckoutDTO checkoutDTO) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: No token provided");
        }

        String token = authorizationHeader.substring(7);
        if (!jwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Invalid token");
        }

        BigDecimal sum = new BigDecimal("0.00");

        List<ItemDTO> items = checkoutDTO.getItems();
        for (ItemDTO item : items) {
            BigDecimal itemPrice = itemService.getItemByNumber(item.getNumber()).getPrice();
            sum = sum.add(itemPrice);
        }

        String newCheckoutNumber = generateRandomNumber();

        CheckoutEntity checkout = new CheckoutEntity();
        checkout.setUserid(checkoutDTO.getUserid());
        checkout.setItems(checkoutDTO.getItems());
        checkout.setSum(sum);
        checkout.setPaid(false);
        checkout.setDate(new Date());
        checkout.setNumber(newCheckoutNumber);

        checkoutRepository.save(checkout);

        return ResponseEntity.ok(checkout);
    }

    private String generateRandomNumber() {
        long randomNum = (long) (Math.floor(Math.random() * (900000000000L)) + 100000000000L);
        return String.format("%d-%d-%d-%d", randomNum / 1000000000, (randomNum / 1000000) % 1000, (randomNum / 1000) % 1000, randomNum % 1000);
    }

    public CheckoutDTO getCheckoutDTOByNumber(String checkoutNumber) {
        Optional<CheckoutEntity> checkoutOpt = checkoutRepository.findByNumber(checkoutNumber);
        return checkoutOpt.map(this::convertToDTO).orElse(null);
    }

    private CheckoutDTO convertToDTO(CheckoutEntity checkoutEntity) {
        CheckoutDTO dto = new CheckoutDTO();
        dto.setId(checkoutEntity.getId());
        dto.setUserid(checkoutEntity.getUserid());
        dto.setItems(checkoutEntity.getItems());
        dto.setSum(checkoutEntity.getSum());
        dto.setPaid(checkoutEntity.isPaid());
        dto.setDate(checkoutEntity.getDate());
        dto.setNumber(checkoutEntity.getNumber());
        return dto;
    }

    @Transactional
    @Scheduled(fixedRate = 7200000)
    public void removeOldCheckouts() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -30);
        Date thirtyMinutesAgo = calendar.getTime();

        List<CheckoutEntity> oldCheckouts = checkoutRepository.findByDateBefore(thirtyMinutesAgo);
        if (!oldCheckouts.isEmpty()) {
            checkoutRepository.deleteAll(oldCheckouts);
        }
    }

}
