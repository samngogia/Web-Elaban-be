package NgoGiaSam.Web_Elaban_be.controller;

import NgoGiaSam.Web_Elaban_be.dao.RoleRespository;
import NgoGiaSam.Web_Elaban_be.dao.UserRespository;
import NgoGiaSam.Web_Elaban_be.enity.Role;
import NgoGiaSam.Web_Elaban_be.enity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {
    private final UserRespository userRespository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RoleRespository roleRespository;
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        List<Map<String, Object>> result = userRespository.findAll().stream().map(u -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", u.getId());
            map.put("username", u.getUsername());
            map.put("email", u.getEmail());
            map.put("firstName", u.getFirstName());
            map.put("lastName", u.getLastName());
            map.put("isEnabled", u.isEnabled());
            map.put("roles", u.getRoles().stream()
                    .map(r -> r.getName()).collect(java.util.stream.Collectors.toList()));
            return map;
        }).collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody Map<String, String> payload) {
        try {
            String username = payload.get("username");
            String email = payload.get("email");
            String password = payload.get("password");
            String firstName = payload.get("firstName");
            String lastName = payload.get("lastName");
            String roleName = payload.get("roleName");

            // Kiểm tra trùng lặp
            if (userRespository.existsByUsername(username)) {
                return ResponseEntity.badRequest().body("Tên đăng nhập đã tồn tại!");
            }
            if (userRespository.existsByEmail(email)) {
                return ResponseEntity.badRequest().body("Email đã tồn tại!");
            }

            // Tạo đối tượng User mới
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setEmail(email);
            // Mã hóa mật khẩu trước khi lưu
            newUser.setPassword(passwordEncoder.encode(password));
            newUser.setFirstName(firstName);
            newUser.setLastName(lastName);
            newUser.setEnabled(true); // Tài khoản mới tạo mặc định được mở khóa

            // Xử lý phân quyền
            Role role = roleRespository.findByName(roleName).orElse(null);
            if (role == null) {
                return ResponseEntity.badRequest().body("Không tìm thấy quyền: " + roleName);
            }

            // Tùy vào Entity của bạn dùng List hay Set mà truyền vào cho đúng
            newUser.setRoles(Collections.singletonList(role));

            // Lưu vào Database
            userRespository.save(newUser);

            return ResponseEntity.ok("Tạo tài khoản thành công!");

        } catch (Exception e) {
            System.err.println("Lỗi tạo user: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Lỗi server: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/toggle-enabled")
    public ResponseEntity<?> toggleEnabled(@PathVariable Long id) {
        return userRespository.findById(id).map(u -> {
            u.setEnabled(!u.isEnabled());
            userRespository.save(u);
            return ResponseEntity.ok("Updated");
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userRespository.deleteById(id);
        return ResponseEntity.ok("Deleted");
    }
}
