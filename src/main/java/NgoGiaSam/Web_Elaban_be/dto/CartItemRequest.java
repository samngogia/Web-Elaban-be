package NgoGiaSam.Web_Elaban_be.dto;

import lombok.Data;

@Data
public class CartItemRequest {
    private Long userId;
    private Long productId;
    private Long quantity;
}
