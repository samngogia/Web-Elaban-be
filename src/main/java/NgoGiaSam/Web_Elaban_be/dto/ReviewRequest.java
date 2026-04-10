package NgoGiaSam.Web_Elaban_be.dto;

import lombok.Data;

@Data
public class ReviewRequest {
    private Long productId;
    private Long userId;
    private int rating;
    private String content;
}
