package NgoGiaSam.Web_Elaban_be.controller;

import NgoGiaSam.Web_Elaban_be.dao.ProductRespository;
import NgoGiaSam.Web_Elaban_be.dao.ReviewRespository;
import NgoGiaSam.Web_Elaban_be.dao.UserRespository;
import NgoGiaSam.Web_Elaban_be.dto.ReviewRequest;
import NgoGiaSam.Web_Elaban_be.enity.Product;
import NgoGiaSam.Web_Elaban_be.enity.Review;
import NgoGiaSam.Web_Elaban_be.enity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewRespository reviewRespository;
    private final ProductRespository productRespository;
    private final UserRespository userRespository;

    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody ReviewRequest request) {
        try {
            Product product = productRespository.findById(request.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            User user = userRespository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Review review = new Review();
            review.setProduct(product);
            review.setUser(user);
            review.setRating(request.getRating());
            review.setContent(request.getContent());
            review.setCreatedDate(LocalDateTime.now());
            review.setApproved(true);  // mặc định chưa duyệt

            reviewRespository.save(review);
            return ResponseEntity.ok("Đánh giá đã được gửi, chờ admin duyệt!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @DeleteMapping("/admin/reviews/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable Long id) {
        reviewRespository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
