package NgoGiaSam.Web_Elaban_be.dto;

import lombok.Data;

import java.util.List;

@Data
public class CheckoutRequest {
    private Long userId;
    private String shippingAddress;
    private String billingAddress;
    private Integer paymentMethodId;
    private Integer shippingMethodId;
    private List<CheckoutItem> items;

    @Data
    public static class CheckoutItem {
        private Long productId;
        private Long quantity;
        private double price;
    }
}
