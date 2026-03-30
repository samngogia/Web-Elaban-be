package NgoGiaSam.Web_Elaban_be.controller;

import NgoGiaSam.Web_Elaban_be.dao.OrderRespository;
import NgoGiaSam.Web_Elaban_be.enity.Order;
import NgoGiaSam.Web_Elaban_be.service.VNPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/vnpay")
@RequiredArgsConstructor
public class VNPayController {
    private final VNPayService vnPayService;
    private final OrderRespository orderRespository;

    // Tạo URL thanh toán
    @PostMapping("/create-payment")
    public ResponseEntity<?> createPayment(@RequestParam Long orderId) {
        try {
            Order order = orderRespository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            String paymentUrl = vnPayService.createPaymentUrl(
                    orderId,
                    order.getTotalAmount(),
                    "Thanh toan don hang " + orderId
            );

            Map<String, String> response = new HashMap<>();
            response.put("paymentUrl", paymentUrl);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    // Nhận kết quả từ VNPay callback
    @GetMapping("/payment-return")
    public ResponseEntity<?> paymentReturn(@RequestParam Map<String, String> params) {
        try {
            boolean isValid = vnPayService.verifyPayment(params);
            String responseCode = params.get("vnp_ResponseCode");
            String txnRef = params.get("vnp_TxnRef");
            Long orderId = Long.parseLong(txnRef.split("_")[0]);

            if (isValid && "00".equals(responseCode)) {
                // Thanh toán thành công — cập nhật trạng thái đơn hàng
                orderRespository.findById(orderId).ifPresent(order -> {
                    order.setPaymentStatus("PAID");
                    orderRespository.save(order);
                });
                return ResponseEntity.ok("Thanh toán thành công!");
            } else {
                return ResponseEntity.badRequest().body("Thanh toán thất bại!");
            }

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
