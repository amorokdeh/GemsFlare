package com.gemsflare.gemsflare.address.controller;

import com.gemsflare.gemsflare.address.jpa.DeliveryAddressEntity;
import com.gemsflare.gemsflare.address.model.DeliveryAddressDTO;
import com.gemsflare.gemsflare.address.service.DeliveryAddressService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/deliveryAddress")
public class DeliveryAddressController {

    @Autowired
    private DeliveryAddressService deliveryAddressService;

    @PostMapping("/addMyDeliveryAddress")
    public ResponseEntity<?> addMyDeliveryAddress(HttpServletRequest request, @RequestBody DeliveryAddressEntity deliveryAddressEntity) {
        return deliveryAddressService.addMyDeliveryAddress(request, deliveryAddressEntity);
    }

    @DeleteMapping("/removeMyDeliveryAddress")
    public ResponseEntity<?> removeMyDeliveryAddress(HttpServletRequest request) {
        return deliveryAddressService.removeMyDeliveryAddress(request);
    }

    @PutMapping("/editMyDeliveryAddress")
    public ResponseEntity<?> editMyDeliveryAddress(HttpServletRequest request, @RequestBody DeliveryAddressEntity updatedAddress) {
        return deliveryAddressService.editMyDeliveryAddress(request, updatedAddress);
    }

    @GetMapping("/getMyDeliveryAddress")
    public ResponseEntity<?> getDeliveryAddress(HttpServletRequest request) {
        return deliveryAddressService.getMyDeliveryAddress(request);
    }

    @GetMapping("/getAllDeliveryAddresses")
    public ResponseEntity<?> getAllDeliveryAddresses(HttpServletRequest request) {
        return deliveryAddressService.getAllDeliveryAddresses(request);
    }

    @PostMapping("/addDeliveryAddressToUserByAdmin")
    public ResponseEntity<?> addDeliveryAddressToUserByAdmin(
            HttpServletRequest request,
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) String username,
            @RequestBody DeliveryAddressDTO addressDTO) {
        return deliveryAddressService.addDeliveryAddressToUserByAdmin(request, userId, username, addressDTO);
    }

    @DeleteMapping("/removeDeliveryAddressFromUserByAdmin")
    public ResponseEntity<?> removeDeliveryAddressFromUserByAdmin(
            HttpServletRequest request,
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) String username) {
        return deliveryAddressService.removeDeliveryAddressFromUserByAdmin(request, userId, username);
    }

}
