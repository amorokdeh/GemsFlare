package com.gemsflare.gemsflare.checkout.controller;

import com.gemsflare.gemsflare.checkout.model.CheckoutDTO;
import com.gemsflare.gemsflare.checkout.service.CheckoutService;
import com.gemsflare.gemsflare.item.model.ItemDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/checkout")
public class CheckoutController {

    @Autowired
    private CheckoutService checkoutService;

    @GetMapping("/getAllCheckout")
    public ResponseEntity<?> getAllCheckout(HttpServletRequest request,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "20") int size) {
        return checkoutService.getAllCheckout(request, page, size);
    }

    @GetMapping("/getCheckout/{checkoutNumber}")
    public ResponseEntity<?> getCheckoutByNumber(@PathVariable String checkoutNumber) {
        return checkoutService.getCheckoutByNumber(checkoutNumber);
    }

    @PostMapping("/addCheckout")
    public ResponseEntity<?> addCheckout(HttpServletRequest request,
                                         @RequestBody CheckoutDTO checkoutDTO) {
        return checkoutService.addCheckout(request, checkoutDTO);
    }

}
