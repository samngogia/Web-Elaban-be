package NgoGiaSam.Web_Elaban_be.controller;

import NgoGiaSam.Web_Elaban_be.dao.CartItemRespository;
import NgoGiaSam.Web_Elaban_be.dto.CartDetailResponse;
import NgoGiaSam.Web_Elaban_be.dto.CartItemRequest;
import NgoGiaSam.Web_Elaban_be.dto.CartResponse;
import NgoGiaSam.Web_Elaban_be.enity.Cart;
import NgoGiaSam.Web_Elaban_be.enity.CartItem;
import NgoGiaSam.Web_Elaban_be.service.CartService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    @Autowired
    private final CartService cartService;

    @Autowired
    private final CartItemRespository cartItemRespository;

    // Thêm vào giỏ
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(
            @RequestParam Long userId,
            @RequestBody CartItemRequest request
    ) {
        request.setUserId(userId);

        Cart cart = cartService.addToCart(request);
        int totalItems = cartItemRespository.findByCart_Id(cart.getId()).size();

        return ResponseEntity.ok(new CartResponse(
                cart.getId(),
                userId,
                totalItems
        ));
    }


    // Lấy giỏ hàng
    @GetMapping("/{userId}")
    @Transactional
    public ResponseEntity<?> getCart(@PathVariable Long userId) {
        Cart cart = cartService.getOrCreateCart(userId);
        List<CartItem> items = cartItemRespository.findByCart_Id(cart.getId());

        List<CartDetailResponse.CartItemResponse> itemResponses = items.stream()
                .map(item -> new CartDetailResponse.CartItemResponse(
                        item.getId(),
                        item.getQuantity(),
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getProduct().getSellingPrice(),
                        item.getProduct().getListPrice()
                ))
                .collect(java.util.stream.Collectors.toList());

        return ResponseEntity.ok(new CartDetailResponse(cart.getId(), userId, itemResponses));
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
