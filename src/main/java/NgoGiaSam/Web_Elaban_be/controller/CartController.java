package NgoGiaSam.Web_Elaban_be.controller;

import NgoGiaSam.Web_Elaban_be.dao.CartItemRespository;
import NgoGiaSam.Web_Elaban_be.dto.CartItemRequest;
import NgoGiaSam.Web_Elaban_be.dto.CartResponse;
import NgoGiaSam.Web_Elaban_be.enity.Cart;
import NgoGiaSam.Web_Elaban_be.enity.CartItem;
import NgoGiaSam.Web_Elaban_be.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final CartItemRespository cartItemRespository;

    // Thêm vào giỏ
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody CartItemRequest request) {
        Cart cart = cartService.addToCart(request);
        int totalItems = cartItemRespository.findByCart_Id(cart.getId()).size();
        return ResponseEntity.ok(new CartResponse(
                cart.getId(),
                request.getUserId(),
                totalItems
        ));
    }


    // Lấy giỏ hàng
    @GetMapping("/{userId}")
    public ResponseEntity<?> getCart(@PathVariable Long userId) {
        cartService.getOrCreateCart(userId);
        return ResponseEntity.ok("OK");
    }

    // Cập nhật số lượng
    @PutMapping("/item/{cartItemId}")
    public ResponseEntity<?> updateQuantity(
            @PathVariable Long cartItemId,
            @RequestParam Long quantity) {
        CartItem item = cartService.updateQuantity(cartItemId, quantity);
        return ResponseEntity.ok(item);
    }

    // Xóa 1 item
    @DeleteMapping("/item/{cartItemId}")
    public ResponseEntity<?> removeItem(@PathVariable Long cartItemId) {
        cartService.removeFromCart(cartItemId);
        return ResponseEntity.ok("Removed successfully");
    }

    // Xóa toàn bộ giỏ
    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<?> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok("Cart cleared");
    }
}
