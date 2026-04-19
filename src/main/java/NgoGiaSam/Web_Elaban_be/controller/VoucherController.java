package NgoGiaSam.Web_Elaban_be.controller;

import NgoGiaSam.Web_Elaban_be.dao.VoucherRepository;
import NgoGiaSam.Web_Elaban_be.enity.Voucher;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/voucher")
@RequiredArgsConstructor
public class VoucherController {
    private final VoucherRepository voucherRepository;

    // ── User: áp dụng mã giảm giá ──────────────────────
    @PostMapping("/apply")
    public ResponseEntity<?> applyVoucher(
            @RequestParam String code,
            @RequestParam double orderAmount) {

        Optional<Voucher> opt = voucherRepository.findByCode(code.toUpperCase().trim());
        if (opt.isEmpty()) {
            return ResponseEntity.badRequest().body("Mã giảm giá không tồn tại!");
        }

        Voucher v = opt.get();

        if (!v.isActive()) {
            return ResponseEntity.badRequest().body("Mã giảm giá đã bị vô hiệu hóa!");
        }
        if (v.getExpiredDate() != null && v.getExpiredDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Mã giảm giá đã hết hạn!");
        }
        if (v.getMaxUses() != null && v.getUsedCount() >= v.getMaxUses()) {
            return ResponseEntity.badRequest().body("Mã giảm giá đã hết lượt sử dụng!");
        }
        if (orderAmount < v.getMinOrderAmount()) {
            return ResponseEntity.badRequest().body(
                    "Đơn hàng tối thiểu " + String.format("%,.0f", v.getMinOrderAmount()) + "đ để dùng mã này!");
        }

        double discount = v.getDiscountType() == Voucher.DiscountType.PERCENT
                ? orderAmount * v.getDiscountValue() / 100
                : v.getDiscountValue();

        // Giảm tối đa không quá tổng đơn hàng
        discount = Math.min(discount, orderAmount);

        Map<String, Object> result = new HashMap<>();
        result.put("code", v.getCode());
        result.put("discountType", v.getDiscountType());
        result.put("discountValue", v.getDiscountValue());
        result.put("discountAmount", discount);
        result.put("finalAmount", orderAmount - discount);
        result.put("voucherId", v.getId());

        return ResponseEntity.ok(result);
    }

    // ── Admin: CRUD voucher ─────────────────────────────
    @GetMapping("/admin")
    public ResponseEntity<?> getAllVouchers() {
        return ResponseEntity.ok(voucherRepository.findAllByOrderByCreatedDateDesc());
    }

    @PostMapping("/admin")
    public ResponseEntity<?> createVoucher(@RequestBody Voucher voucher) {
        voucher.setCode(voucher.getCode().toUpperCase().trim());
        return ResponseEntity.ok(voucherRepository.save(voucher));
    }

    @PutMapping("/admin/{id}")
    public ResponseEntity<?> updateVoucher(@PathVariable Long id, @RequestBody Voucher body) {
        return voucherRepository.findById(id).map(v -> {
            v.setCode(body.getCode().toUpperCase().trim());
            v.setDiscountType(body.getDiscountType());
            v.setDiscountValue(body.getDiscountValue());
            v.setMinOrderAmount(body.getMinOrderAmount());
            v.setMaxUses(body.getMaxUses());
            v.setExpiredDate(body.getExpiredDate());
            v.setActive(body.isActive());
            return ResponseEntity.ok(voucherRepository.save(v));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<?> deleteVoucher(@PathVariable Long id) {
        voucherRepository.deleteById(id);
        return ResponseEntity.ok("Đã xóa!");
    }

    // Dùng sau khi đặt hàng thành công
    @PostMapping("/use/{code}")
    public ResponseEntity<?> useVoucher(@PathVariable String code) {
        return voucherRepository.findByCode(code.toUpperCase()).map(v -> {
            v.setUsedCount(v.getUsedCount() + 1);
            voucherRepository.save(v);
            return ResponseEntity.ok("OK");
        }).orElse(ResponseEntity.notFound().build());
    }
}
