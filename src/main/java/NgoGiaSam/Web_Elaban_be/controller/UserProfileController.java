package NgoGiaSam.Web_Elaban_be.controller;

import NgoGiaSam.Web_Elaban_be.dao.UserRespository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class UserProfileController {


    private final UserRespository userRespository;
    private final BCryptPasswordEncoder passwordEncoder;

    // Lấy thông tin profile
    @GetMapping("/{userId}")
    @Transactional
    public ResponseEntity<?> getProfile(@PathVariable Long userId) {
        return userRespository.findById(userId).map(u -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", u.getId());
            map.put("firstName", u.getFirstName());
            map.put("lastName", u.getLastName());
            map.put("username", u.getUsername());
            map.put("email", u.getEmail());
            map.put("gender", u.getGender());
            map.put("shippingAddress", u.getShippingAddress());
            map.put("billingAddress", u.getBillingAddress());
            map.put("avatar", u.getAvatar());
            return ResponseEntity.ok(map);
        }).orElse(ResponseEntity.notFound().build());
    }

    // Cập nhật thông tin profile
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateProfile(
            @PathVariable Long userId,
            @RequestBody Map<String, Object> body) {
        return userRespository.findById(userId).map(u -> {
            if (body.get("firstName") != null)
                u.setFirstName((String) body.get("firstName"));
            if (body.get("lastName") != null)
                u.setLastName((String) body.get("lastName"));
            if (body.get("email") != null)
                u.setEmail((String) body.get("email"));
            if (body.get("gender") != null)
                u.setGender((String) body.get("gender"));
            if (body.get("shippingAddress") != null)
                u.setShippingAddress((String) body.get("shippingAddress"));
            if (body.get("billingAddress") != null)
                u.setBillingAddress((String) body.get("billingAddress"));
            if (body.get("avatar") != null)
                u.setAvatar((String) body.get("avatar"));
            userRespository.save(u);
            return ResponseEntity.ok("Cập nhật thành công!");
        }).orElse(ResponseEntity.notFound().build());
    }

    // Đổi mật khẩu
    @PutMapping("/{userId}/change-password")
    public ResponseEntity<?> changePassword(
            @PathVariable Long userId,
            @RequestBody Map<String, String> body) {
        return userRespository.findById(userId).map(u -> {
            String oldPassword = body.get("oldPassword");
            String newPassword = body.get("newPassword");

            if (!passwordEncoder.matches(oldPassword, u.getPassword())) {
                return ResponseEntity.badRequest().body("Mật khẩu cũ không đúng!");
            }
            u.setPassword(passwordEncoder.encode(newPassword));
            userRespository.save(u);
            return ResponseEntity.ok("Đổi mật khẩu thành công!");
        }).orElse(ResponseEntity.notFound().build());
    }
}
