package NgoGiaSam.Web_Elaban_be.service;

import NgoGiaSam.Web_Elaban_be.dao.ReviewRespository;
import NgoGiaSam.Web_Elaban_be.enity.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import java.util.List;


@Service
public class ReviewAdminService {
    @Autowired
    private ReviewRespository reviewRespository;

    public List<Review> getAllReviews() {
        return reviewRespository.findAll();
    }

    public Review approveReview(Long id) {
        Review review = reviewRespository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        review.setApproved(true);
        return reviewRespository.save(review);
    }

    public Review hideReview(Long id) {
        Review review = reviewRespository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        review.setHidden(true);
        return reviewRespository.save(review);
    }

    public Review showReview(Long id) {
        Review review = reviewRespository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        review.setHidden(false);
        return reviewRespository.save(review);
    }

    public Review replyReview(Long id, String reply) {
        Review review = reviewRespository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        review.setAdminReply(reply);
        review.setReplyDate(LocalDateTime.now());

        return reviewRespository.save(review);
    }
}
