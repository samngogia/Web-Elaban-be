package NgoGiaSam.Web_Elaban_be.controller;

import NgoGiaSam.Web_Elaban_be.dao.OrderDetailRespository;
import NgoGiaSam.Web_Elaban_be.dao.OrderRespository;
import NgoGiaSam.Web_Elaban_be.dto.CheckoutRequest;
import NgoGiaSam.Web_Elaban_be.dto.OrderResponse;
import NgoGiaSam.Web_Elaban_be.enity.Order;
import NgoGiaSam.Web_Elaban_be.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
//@RequestMapping("/order")
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderRespository orderRespository;
    private final OrderDetailRespository orderDetailRespository;
    @GetMapping
    @Transactional
    public ResponseEntity<?> getAllOrders() {
        List<Order> orders = orderRespository.findAll()
                .stream()
                .sorted((a, b) -> {
                    if (a.getCreatedDate() == null) return 1;
                    if (b.getCreatedDate() == null) return -1;
                    return b.getCreatedDate().compareTo(a.getCreatedDate());
                })
                .toList();

        List<Map<String, Object>> result = orders.stream().map(o -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", o.getId());
            map.put("fullName", o.getFullName());
            map.put("phoneNumber", o.getPhoneNumber());
            map.put("shippingAddress", o.getShippingAddress());
            map.put("paymentStatus", o.getPaymentStatus());
            map.put("shippingStatus", o.getShippingStatus());
            map.put("totalAmount", o.getTotalAmount());
            map.put("createdDate", o.getCreatedDate());
            map.put("note", o.getNote());
            map.put("username", o.getUser() != null ? o.getUser().getUsername() : "");

            map.put("paymentMethod", o.getPaymentMethod() != null ? o.getPaymentMethod().getName() : "");

            map.put("orderDetails", o.getOrderDetails().stream().map(d -> {
                Map<String, Object> detail = new HashMap<>();
                detail.put("productId", d.getProduct().getId());
                detail.put("productName", d.getProduct().getName());
                detail.put("quantity", d.getQuantity());
                detail.put("price", d.getPrice());
                return detail;
            }).toList());
            return map;
        }).toList();

        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{id}/payment-status")
    public ResponseEntity<?> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return orderRespository.findById(id).map(order -> {
            order.setPaymentStatus(status);
            orderRespository.save(order);
            return ResponseEntity.ok("Cập nhật thành công");
        }).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/shipping-status")
    public ResponseEntity<?> updateShippingStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return orderRespository.findById(id).map(order -> {
            order.setShippingStatus(status);
            orderRespository.save(order);
            return ResponseEntity.ok("Cập nhật thành công");
        }).orElse(ResponseEntity.notFound().build());
    }



}
