package NgoGiaSam.Web_Elaban_be.controller;

import NgoGiaSam.Web_Elaban_be.dao.ProductRespository;
import NgoGiaSam.Web_Elaban_be.dao.UserRespository;
import NgoGiaSam.Web_Elaban_be.dao.WishListRespository;
import NgoGiaSam.Web_Elaban_be.enity.Product;
import NgoGiaSam.Web_Elaban_be.enity.User;
import NgoGiaSam.Web_Elaban_be.enity.WishList;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishListController {
    private final WishListRespository wishListRespository;
    private final UserRespository userRespository;
    private final ProductRespository productRespository;

    // Lấy danh sách yêu thích
    @GetMapping("/{userId}")
    @Transactional
    public ResponseEntity<?> getWishList(@PathVariable Long userId) {
        List<WishList> list = wishListRespository.findByUser_Id(userId);
        List<Map<String, Object>> result = list.stream().map(w -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", w.getId());
            map.put("productId", w.getProduct().getId());
            map.put("productName", w.getProduct().getName());
            map.put("sellingPrice", w.getProduct().getSellingPrice());
            map.put("listPrice", w.getProduct().getListPrice());
            map.put("brand", w.getProduct().getBrand());
            return map;
        }).collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(result);
    }

    // Thêm vào yêu thích
    @PostMapping("/add")
    public ResponseEntity<?> addToWishList(
            @RequestParam Long userId,
            @RequestParam Long productId) {

        if (wishListRespository.existsByUser_IdAndProduct_Id(userId, productId)) {
            return ResponseEntity.badRequest().body("Sản phẩm đã có trong danh sách yêu thích!");
        }

        User user = userRespository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRespository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        WishList wishList = new WishList();
        wishList.setUser(user);
        wishList.setProduct(product);
        wishListRespository.save(wishList);

        return ResponseEntity.ok("Đã thêm vào yêu thích!");
    }

    // Xóa khỏi yêu thích
    @DeleteMapping("/remove")
    @Transactional
    public ResponseEntity<?> removeFromWishList(
            @RequestParam Long userId,
            @RequestParam Long productId) {
        wishListRespository.deleteByUser_IdAndProduct_Id(userId, productId);
        return ResponseEntity.ok("Đã xóa khỏi yêu thích!");
    }

    // Kiểm tra sản phẩm có trong yêu thích không
    @GetMapping("/check")
    public ResponseEntity<?> checkWishList(
            @RequestParam Long userId,
            @RequestParam Long productId) {
        boolean exists = wishListRespository.existsByUser_IdAndProduct_Id(userId, productId);
        return ResponseEntity.ok(exists);
    }
}
