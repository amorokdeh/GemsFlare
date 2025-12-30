package com.gemsflare.gemsflare.address.controller;

import com.gemsflare.gemsflare.address.jpa.BillAddressEntity;
import com.gemsflare.gemsflare.address.model.BillAddressDTO;
import com.gemsflare.gemsflare.address.service.BillAddressService;
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
@RequestMapping("/billAddress")
public class BillAddressController {

    @Autowired
    private BillAddressService billAddressService;

    @PostMapping("/addMyBillAddress")
    public ResponseEntity<?> addMyBillAddress(HttpServletRequest request, @RequestBody BillAddressEntity billAddressEntity) {
        return billAddressService.addMyBillAddress(request, billAddressEntity);
    }

    @DeleteMapping("/removeMyBillAddress")
    public ResponseEntity<?> removeMyBillAddress(HttpServletRequest request) {
        return billAddressService.removeMyBillAddress(request);
    }

    @PutMapping("/editMyBillAddress")
    public ResponseEntity<?> editMyBillAddress(HttpServletRequest request, @RequestBody BillAddressEntity updatedAddress) {
        return billAddressService.editMyBillAddress(request, updatedAddress);
    }

    @GetMapping("/getMyBillAddress")
    public ResponseEntity<?> getDeliveryAddress(HttpServletRequest request) {
        return billAddressService.getMyBillAddress(request);
    }

    @GetMapping("/getAllBillAddresses")
    public ResponseEntity<?> getAllBillAddresses(HttpServletRequest request) {
        return billAddressService.getAllBillAddresses(request);
    }

    @PostMapping("/addBillAddressToUserByAdmin")
    public ResponseEntity<?> addBillAddressToUserByAdmin(
            HttpServletRequest request,
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) String username,
            @RequestBody BillAddressDTO addressDTO) {
        return billAddressService.addBillAddressToUserByAdmin(request, userId, username, addressDTO);
    }

    @DeleteMapping("/removeBillAddressFromUserByAdmin")
    public ResponseEntity<?> removeBillAddressFromUserByAdmin(
            HttpServletRequest request,
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) String username) {
        return billAddressService.removeBillAddressFromUserByAdmin(request, userId, username);
    }

}
