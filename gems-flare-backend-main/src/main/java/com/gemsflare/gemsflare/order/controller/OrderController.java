package com.gemsflare.gemsflare.order.controller;

import com.gemsflare.gemsflare.checkout.model.CheckoutDTO;
import com.gemsflare.gemsflare.order.service.OrderService;
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

import java.util.UUID;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/getAllOrders")
    public ResponseEntity<?> getAllOrders(HttpServletRequest request,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "20") int size) {
        return orderService.getAllOrders(request, page, size);
    }

    @GetMapping("/getOrderByNumber/{orderNumber}")
    public ResponseEntity<?> getOrderByNumber(@PathVariable String orderNumber) {
        return orderService.getOrderByNumber(orderNumber);
    }

    @GetMapping("/getOrderByTransaction/{transaction}")
    public ResponseEntity<?> getOrderByTransaction(@PathVariable String transaction) {
        return orderService.getOrderByTransaction(transaction);
    }

    @GetMapping("/getOrdersByUserid/{userid}")
    public ResponseEntity<?> getOrdersByUserid(@PathVariable UUID userid,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "20") int size) {
        return orderService.getOrdersByUserid(userid, page, size);
    }

    @PostMapping("/addOrder")
    public ResponseEntity<?> addOrder(HttpServletRequest request,
                                      @RequestBody CheckoutDTO checkoutDTO,
                                      @RequestBody String transaction) {
        return orderService.addOrder(request, checkoutDTO, transaction);
    }

    @PostMapping("/cancelOrder/{orderNumber}")
    public ResponseEntity<?> cancelOrder(HttpServletRequest request,
                                         @PathVariable String orderNumber) {
        return orderService.cancelOrderByNumber(request, orderNumber);
    }

}
