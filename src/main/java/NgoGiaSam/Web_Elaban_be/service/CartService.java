package NgoGiaSam.Web_Elaban_be.service;

import NgoGiaSam.Web_Elaban_be.dao.CartItemRespository;
import NgoGiaSam.Web_Elaban_be.dao.CartRespository;
import NgoGiaSam.Web_Elaban_be.dao.ProductRespository;
import NgoGiaSam.Web_Elaban_be.dao.UserRespository;
import NgoGiaSam.Web_Elaban_be.dto.CartItemRequest;
import NgoGiaSam.Web_Elaban_be.enity.Cart;
import NgoGiaSam.Web_Elaban_be.enity.CartItem;
import NgoGiaSam.Web_Elaban_be.enity.Product;
import NgoGiaSam.Web_Elaban_be.enity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Transactional
@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRespository cartRespository;
    private final CartItemRespository cartItemRespository;
    private final ProductRespository productRespository;
    private final UserRespository userRespository;

    // Lấy hoặc tạo mới cart cho user
    public Cart getOrCreateCart(Long userId) {
        return cartRespository.findByUser_Id(userId)
                .orElseGet(() -> {
                    User user = userRespository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("User not found"));
                    Cart cart = new Cart();
                    cart.setUser(user);
                    return cartRespository.save(cart);
                });
    }

    // Thêm sản phẩm vào giỏ
    public Cart addToCart(CartItemRequest request) {
        Cart cart = getOrCreateCart(request.getUserId());
        Product product = productRespository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Nếu sản phẩm đã có trong giỏ thì cộng thêm số lượng
        Optional<CartItem> existingItem = cartItemRespository
                .findByCart_IdAndProduct_Id(cart.getId(), product.getId());

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
            cartItemRespository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(request.getQuantity());
            cartItemRespository.save(newItem);
        }

        return cartRespository.findById(cart.getId()).orElseThrow();
    }

    // Xóa 1 item khỏi giỏ
    public void removeFromCart(Long cartItemId) {
        cartItemRespository.deleteById(cartItemId);
    }

    // Cập nhật số lượng
    public CartItem updateQuantity(Long cartItemId, Long quantity) {
        CartItem item = cartItemRespository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("CartItem not found"));
        if (quantity <= 0) {
            cartItemRespository.delete(item);
            return null;
        }
        item.setQuantity(quantity);
        return cartItemRespository.save(item);
    }

    // Xóa toàn bộ giỏ hàng
    public void clearCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        cart.getCartItems().clear();
        cartRespository.save(cart);
    }
}
