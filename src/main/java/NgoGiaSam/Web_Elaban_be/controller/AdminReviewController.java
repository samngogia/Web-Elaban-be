package NgoGiaSam.Web_Elaban_be.controller;

import NgoGiaSam.Web_Elaban_be.dto.ReplyReviewRequest;
import NgoGiaSam.Web_Elaban_be.dto.ReviewAdminResponse;
import NgoGiaSam.Web_Elaban_be.enity.Review;
import NgoGiaSam.Web_Elaban_be.service.ReviewAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/reviews")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminReviewController {
    @Autowired
    private ReviewAdminService reviewAdminService;

    private ReviewAdminResponse mapToDto(Review review) {
        return new ReviewAdminResponse(
                review.getId(),
                review.getUser().getUsername(),
                review.getProduct().getName(),
                (int) review.getRating(),
                review.getContent(),
                review.isApproved(),
                !review.isHidden(),
                review.getAdminReply(),
                review.getCreatedDate(),
                review.getReplyDate()
        );
    }

    @GetMapping
    public List<ReviewAdminResponse> getAllReviews() {
        return reviewAdminService.getAllReviews()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @PutMapping("/{id}/approve")
    public ReviewAdminResponse approveReview(@PathVariable Long id) {
        return mapToDto(reviewAdminService.approveReview(id));
    }

    @PutMapping("/{id}/hide")
    public ReviewAdminResponse hideReview(@PathVariable Long id) {
        return mapToDto(reviewAdminService.hideReview(id));
    }

    @PutMapping("/{id}/show")
    public ReviewAdminResponse showReview(@PathVariable Long id) {
        return mapToDto(reviewAdminService.showReview(id));
    }

    @PutMapping("/{id}/reply")
    public ReviewAdminResponse replyReview(
            @PathVariable Long id,
            @RequestBody ReplyReviewRequest request
    ) {
        return mapToDto(reviewAdminService.replyReview(id, request.getReply()));
    }
}
