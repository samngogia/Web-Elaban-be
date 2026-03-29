package NgoGiaSam.Web_Elaban_be.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class OrderResponse {
    private Long orderId;
    private double totalAmount;
    private String paymentStatus;
    private String shippingStatus;
}
