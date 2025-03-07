package com.ecom.service.impl;

import com.ecom.model.Review;
import com.ecom.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    public Review createReview(Review review) {
        review.setCreatedAt(new Date());
        return reviewRepository.save(review);
    }

    public List<Review> getReviewsByProductId(Integer productId) {
        return reviewRepository.findByProductId(productId);
    }

    // Các phương thức khác liên quan đến review nếu cần

    // Thêm phương thức getReviewById để lấy một đánh giá theo ID
    public Review getReviewById(Integer reviewId) {
        return reviewRepository.findById(reviewId).orElse(null); // Trả về null nếu không tìm thấy đánh giá
    }


}
