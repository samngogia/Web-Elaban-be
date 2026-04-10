package NgoGiaSam.Web_Elaban_be.controller;

import NgoGiaSam.Web_Elaban_be.dao.OrderRespository;
import NgoGiaSam.Web_Elaban_be.dao.ProductRespository;
import NgoGiaSam.Web_Elaban_be.dao.UserRespository;
import NgoGiaSam.Web_Elaban_be.enity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final OrderRespository orderRespository;
    private final ProductRespository productRespository;
    private final UserRespository userRespository;

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalOrders", orderRespository.count());
        stats.put("totalProducts", productRespository.count());
        stats.put("totalUsers", userRespository.count());
        stats.put("totalRevenue", orderRespository.getTotalRevenue() != null
                ? orderRespository.getTotalRevenue() : 0);
        stats.put("pendingOrders", orderRespository.findByShippingStatus("PENDING").size());
        stats.put("paidOrders", orderRespository.findByPaymentStatus("PAID").size());

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/recent-orders")
    public ResponseEntity<?> getRecentOrders() {
        List<Order> orders = orderRespository.findTop5ByOrderByCreatedDateDesc();
        List<Map<String, Object>> result = orders.stream().map(o -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", o.getId());
            map.put("totalAmount", o.getTotalAmount());
            map.put("paymentStatus", o.getPaymentStatus());
            map.put("shippingStatus", o.getShippingStatus());
            map.put("createdDate", o.getCreatedDate());
            map.put("userId", o.getUser().getId());
            return map;
        }).collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/revenue-by-month")
    public ResponseEntity<?> getRevenueByMonth(@RequestParam(defaultValue = "2026") int year) {
        List<Object[]> data = orderRespository.getRevenueByMonth(year);
        List<Map<String, Object>> result = data.stream().map(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("month", row[0]);
            map.put("revenue", row[1]);
            return map;
        }).collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(result);
    }
}
