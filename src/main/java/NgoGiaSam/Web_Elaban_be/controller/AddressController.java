package NgoGiaSam.Web_Elaban_be.controller;

import NgoGiaSam.Web_Elaban_be.dao.UserRespository;
import NgoGiaSam.Web_Elaban_be.dto.AddressRequest;
import NgoGiaSam.Web_Elaban_be.enity.Address;
import NgoGiaSam.Web_Elaban_be.enity.User;
import NgoGiaSam.Web_Elaban_be.service.AddressService;
import NgoGiaSam.Web_Elaban_be.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;
    private final UserRespository userRespository;
    private final JwtService jwtService;  // thêm inject

    private Long getCurrentUserId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Unauthorized");
        }
        String token = authHeader.substring(7);
        String username = jwtService.extracUsername(token);
        return userRespository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }

    @GetMapping
    @Transactional
    public ResponseEntity<?> getMyAddresses(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        List<Address> list = addressService.getAddressesByUser(userId);
        List<Map<String, Object>> result = list.stream().map(this::mapAddress)
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> createAddress(
            @RequestBody AddressRequest body,
            HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        Address saved = addressService.createAddress(userId, body);
        return ResponseEntity.ok(mapAddress(saved));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> updateAddress(
            @PathVariable Long id,
            @RequestBody AddressRequest body,
            HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        Address updated = addressService.updateAddress(id, userId, body);
        return ResponseEntity.ok(mapAddress(updated));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteAddress(
            @PathVariable Long id,
            HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        addressService.deleteAddress(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/default")
    @Transactional
    public ResponseEntity<?> setDefaultAddress(
            @PathVariable Long id,
            HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        addressService.setDefaultAddress(id, userId);
        return ResponseEntity.ok("Updated");
    }

    // Map sang Map để tránh lazy load
    private Map<String, Object> mapAddress(Address a) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", a.getId());
        map.put("fullName", a.getFullName());
        map.put("phone", a.getPhone());
        map.put("province", a.getProvince());
        map.put("district", a.getDistrict());
        map.put("ward", a.getWard());
        map.put("addressLine", a.getAddressLine());
        map.put("addressType", a.getAddressType());
        map.put("note", a.getNote());
        map.put("isDefault", a.getIsDefault());
        return map;
    }
}