package com.gemsflare.gemsflare.order.service;

import com.gemsflare.gemsflare.address.service.BillAddressService;
import com.gemsflare.gemsflare.address.service.DeliveryAddressService;
import com.gemsflare.gemsflare.checkout.model.CheckoutDTO;
import com.gemsflare.gemsflare.invoice.model.InvoiceDTO;
import com.gemsflare.gemsflare.invoice.service.InvoiceService;
import com.gemsflare.gemsflare.item.model.ItemDTO;
import com.gemsflare.gemsflare.item.service.ItemService;
import com.gemsflare.gemsflare.order.jpa.OrderEntity;
import com.gemsflare.gemsflare.order.model.OrderDTO;
import com.gemsflare.gemsflare.order.repository.OrderRepository;
import com.gemsflare.gemsflare.payment.service.PaypalRefundService;
import com.gemsflare.gemsflare.permission.service.PermissionService;
import com.gemsflare.gemsflare.security.JwtUtil;
import com.gemsflare.gemsflare.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {
    @Autowired
    private ItemService itemService;
    @Autowired
    private InvoiceService invoiceService;
    @Autowired
    private BillAddressService billAddressService;
    @Autowired
    private DeliveryAddressService deliveryAddressService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private PaypalRefundService paypalRefundService;

    public ResponseEntity<?> getAllOrders(HttpServletRequest request, int page, int size) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Unauthorized: No token provided");
        }

        String token = authorizationHeader.substring(7);
        if (!jwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid token");
        }

        if (!permissionService.hasPermission(request, "/order")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Permission required");
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderEntity> orderPage = orderRepository.findAll(pageable);

        if (orderPage.isEmpty()) {
            return ResponseEntity.status(404).body("No Order process found");
        }

        return ResponseEntity.ok(orderPage);
    }

    public ResponseEntity<?> getOrderByNumber(String orderNumber) {
        Optional<OrderEntity> orderOpt = orderRepository.findByNumber(orderNumber);
        if (orderOpt.isPresent()) {
            return ResponseEntity.ok(orderOpt.get());
        } else {
            return ResponseEntity.status(404).body("Order not found with number: " + orderNumber);
        }
    }

    public ResponseEntity<?> cancelOrderByNumber(HttpServletRequest request, String orderNumber) {

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Unauthorized: No token provided");
        }

        String token = authorizationHeader.substring(7);
        if (!jwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid token");
        }

        UUID requesterId = userService.getUserIdFromRequest(request);
        Optional<OrderEntity> orderOpt = orderRepository.findByNumber(orderNumber);

        if (orderOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Order not found with number: " + orderNumber);
        }

        OrderEntity order = orderOpt.get();

        if (permissionService.hasPermission(request, "/order") || requesterId.equals(order.getUserid())) {

            try {

                if (order.getState().equals("Waiting") || permissionService.hasPermission(request, "/order")){
                    paypalRefundService.refundOrder(order.getTransaction(), order.getSum().toString());
                    order.setState("Canceled");
                    orderRepository.save(order);
                    return ResponseEntity.ok("Order has been canceled and refunded.");
                }

            } catch (IOException e) {
                return ResponseEntity.status(500).body("Failed to refund order via PayPal: " + e.getMessage());
            } catch (Exception e) {
                return ResponseEntity.status(500).body("Unexpected error during cancellation: " + e.getMessage());
            }
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Permission required");
    }

    public ResponseEntity<?> getOrderByTransaction(String transaction) {
        Optional<OrderEntity> orderOpt = orderRepository.findByTransaction(transaction);
        if (orderOpt.isPresent()) {
            return ResponseEntity.ok(orderOpt.get());
        } else {
            return ResponseEntity.status(404).body("Order not found with transaction: " + transaction);
        }
    }

    public ResponseEntity<?> getOrdersByUserid(UUID userid, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderEntity> orderPage = orderRepository.findAllByUserid(userid, pageable);
        if (orderPage.isEmpty()) {
            return ResponseEntity.status(404).body("No Order process found");
        }

        return ResponseEntity.ok(orderPage);
    }

    public ResponseEntity<?> addOrder(HttpServletRequest request, CheckoutDTO checkoutDTO, String transaction) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: No token provided");
        }

        String token = authorizationHeader.substring(7);
        if (!jwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Invalid token");
        }

        List<ItemDTO> items = checkoutDTO.getItems();
        for (ItemDTO item : items) {
            itemService.changeItemAmount(request, item.getNumber(), - item.getAmount());
        }

        String orderNumber = generateRandomNumber();

        OrderEntity order = new OrderEntity();
        order.setUserid(checkoutDTO.getUserid());
        order.setItems(checkoutDTO.getItems());
        order.setSum(checkoutDTO.getSum());
        order.setDate(new Date());
        order.setNumber(orderNumber);
        order.setState("Waiting");
        order.setTransaction(transaction);

        orderRepository.save(order);

        InvoiceDTO invoiceDTO = new InvoiceDTO();
        invoiceDTO.setOrdernumber(order.getNumber());
        invoiceDTO.setBilladdress(billAddressService.getMyBillAddressDTO(request));
        invoiceDTO.setShippingaddress(deliveryAddressService.getMyDeliveryAddressDTO(request));
        invoiceDTO.setItems(order.getItems());
        invoiceDTO.setTotalamount(order.getSum());
        invoiceDTO.setTotalamountwithouttax(
                order.getSum().multiply(new BigDecimal("0.81")).setScale(2, RoundingMode.HALF_UP)
        );
        invoiceDTO.setTax("19%");
        invoiceDTO.setPayment("PayPall");

        invoiceService.addInvoice(request,invoiceDTO);

        return ResponseEntity.ok(order);
    }

    private String generateRandomNumber() {
        long randomNum = (long) (Math.floor(Math.random() * (900000000000L)) + 100000000000L);
        return String.format("%d-%d-%d-%d", randomNum / 1000000000, (randomNum / 1000000) % 1000, (randomNum / 1000) % 1000, randomNum % 1000);
    }

    public OrderDTO getOrderDTOByTransaction(String transaction) {
        Optional<OrderEntity> orderOpt = orderRepository.findByTransaction(transaction);
        return orderOpt.map(this::convertToDTO).orElse(null);
    }

    private OrderDTO convertToDTO(OrderEntity orderEntity) {
        OrderDTO dto = new OrderDTO();
        dto.setId(orderEntity.getId());
        dto.setNumber(orderEntity.getNumber());
        dto.setItems(orderEntity.getItems());
        dto.setSum(orderEntity.getSum());
        dto.setUserid(orderEntity.getUserid());
        dto.setDate(orderEntity.getDate());
        dto.setState(orderEntity.getState());
        dto.setTransaction(orderEntity.getTransaction());
        return dto;
    }
}
