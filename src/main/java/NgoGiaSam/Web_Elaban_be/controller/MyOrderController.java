package NgoGiaSam.Web_Elaban_be.controller;

import NgoGiaSam.Web_Elaban_be.dao.OrderRespository;
import NgoGiaSam.Web_Elaban_be.dao.UserRespository;
import NgoGiaSam.Web_Elaban_be.enity.Order;
import NgoGiaSam.Web_Elaban_be.enity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class MyOrderController {
    private final OrderRespository orderRespository;
    private final UserRespository userRespository;

    @GetMapping("/my-orders")
    public ResponseEntity<?> getMyOrders(Authentication authentication) {

        String email = authentication.getName();

        User user = userRespository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Order> orders = orderRespository.findByUser_Id(user.getId())
                .stream()
                .sorted((a, b) -> b.getCreatedDate().compareTo(a.getCreatedDate()))
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

            map.put("orderDetails", o.getOrderDetails().stream().map(d -> {
                Map<String, Object> detail = new HashMap<>();
                detail.put("productName", d.getProduct().getName());
                detail.put("quantity", d.getQuantity());
                detail.put("price", d.getPrice());
                return detail;
            }).toList());

            return map;
        }).toList();

        return ResponseEntity.ok(result);
    }
}
