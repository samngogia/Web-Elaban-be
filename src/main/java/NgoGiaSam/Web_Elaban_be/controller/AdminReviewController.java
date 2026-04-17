package NgoGiaSam.Web_Elaban_be.controller;

import NgoGiaSam.Web_Elaban_be.dao.ProductRespository;
import NgoGiaSam.Web_Elaban_be.dao.ReviewRespository;
import NgoGiaSam.Web_Elaban_be.dto.ReplyReviewRequest;
import NgoGiaSam.Web_Elaban_be.dto.ReviewAdminResponse;
import NgoGiaSam.Web_Elaban_be.enity.Review;
import NgoGiaSam.Web_Elaban_be.service.ReviewAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/reviews")
@RequiredArgsConstructor
public class AdminReviewController {
    private final ReviewRespository reviewRespository;
    private final ProductRespository productRespository;

    // Lấy tất cả review
    @GetMapping
    @Transactional
    public ResponseEntity<?> getAllReviews() {
        List<Review> reviews = reviewRespository.findAllByOrderByCreatedDateDesc();
        return ResponseEntity.ok(mapReviews(reviews));
    }

    // Từ chối / xóa review
    // 2. Xóa review và cập nhật lại điểm trung bình sản phẩm
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> deleteReview(@PathVariable Long id) {
        return reviewRespository.findById(id).map(review -> {
            Long productId = review.getProduct().getId();
            // Xóa đánh giá
            reviewRespository.delete(review);
            // TÍNH LẠI ĐIỂM TRUNG BÌNH:
            // Gọi đúng tên hàm findByProduct_IdOrderByCreatedDateDesc có trong Repository của bạn
            List<Review> remainingReviews = reviewRespository.findByProduct_IdOrderByCreatedDateDesc(productId);

            double avg = remainingReviews.stream()
                    .mapToDouble(Review::getRating)
                    .average()
                    .orElse(0.0);

            // Cập nhật lại vào bảng Product
            productRespository.findById(productId).ifPresent(p -> {
                p.setAvgRating(avg);
                productRespository.save(p);
            });

            return ResponseEntity.ok("Đã xóa đánh giá và cập nhật lại điểm sản phẩm!");
        }).orElse(ResponseEntity.notFound().build());
    }

    private List<Map<String, Object>> mapReviews(List<Review> reviews) {
        return reviews.stream().map(r -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", r.getId());
            map.put("content", r.getContent());
            map.put("rating", r.getRating());
            map.put("approved", r.getApproved());
            map.put("createdDate", r.getCreatedDate());
            map.put("productId", r.getProduct().getId());
            map.put("productName", r.getProduct().getName());
            map.put("username", r.getUser().getUsername());
            return map;
        }).collect(java.util.stream.Collectors.toList());
    }
}
