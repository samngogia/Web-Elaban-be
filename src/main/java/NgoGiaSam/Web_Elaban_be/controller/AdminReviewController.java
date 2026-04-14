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

    // Lấy tất cả review chờ duyệt
    @GetMapping("/pending")
    @Transactional
    public ResponseEntity<?> getPendingReviews() {
        List<Review> reviews = reviewRespository.findByApprovedFalse();
        return ResponseEntity.ok(mapReviews(reviews));
    }

    // Lấy tất cả review
    @GetMapping
    @Transactional
    public ResponseEntity<?> getAllReviews() {
        List<Review> reviews = reviewRespository.findAllByOrderByCreatedDateDesc();
        return ResponseEntity.ok(mapReviews(reviews));
    }

    // Duyệt review
    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveReview(@PathVariable Long id) {
        return reviewRespository.findById(id).map(r -> {
            r.setApproved(true);
            reviewRespository.save(r);

            // Cập nhật avgRating
            List<Review> approved = reviewRespository
                    .findByProduct_IdAndApprovedTrue(r.getProduct().getId());
            double avg = approved.stream()
                    .mapToDouble(Review::getRating).average().orElse(0);
            r.getProduct().setAvgRating(avg);
            productRespository.save(r.getProduct());

            return ResponseEntity.ok("Đã duyệt!");
        }).orElse(ResponseEntity.notFound().build());
    }

    // Từ chối / xóa review
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable Long id) {
        reviewRespository.deleteById(id);
        return ResponseEntity.ok("Đã xóa!");
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
