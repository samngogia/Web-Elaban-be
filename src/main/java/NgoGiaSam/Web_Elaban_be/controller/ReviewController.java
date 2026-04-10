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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


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

            reviewRespository.save(review);

            // Cập nhật avgRating của sản phẩm
            List<Review> reviews = reviewRespository.findByProduct_Id(product.getId());
            double avg = reviews.stream().mapToDouble(Review::getRating).average().orElse(0);
            product.setAvgRating(avg);
            productRespository.save(product);

            return ResponseEntity.ok("Đánh giá thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
