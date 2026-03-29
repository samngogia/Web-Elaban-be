package NgoGiaSam.Web_Elaban_be.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CartDetailResponse {
    private Long cartId;
    private Long userId;
    private List<CartItemResponse> cartItems;

    @Data
    @AllArgsConstructor
    public static class CartItemResponse {
        private Long id;
        private Long quantity;
        private Long productId;
        private String productName;
        private double sellingPrice;
        private double listPrice;
    }
}
