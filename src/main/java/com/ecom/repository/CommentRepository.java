package com.ecom.repository;

import com.ecom.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByReviewId(Integer reviewId); // Tìm các bình luận theo review


}
