package NgoGiaSam.Web_Elaban_be.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartResponse {
    private Long cartId;
    private Long userId;
    private int totalItems;

}
