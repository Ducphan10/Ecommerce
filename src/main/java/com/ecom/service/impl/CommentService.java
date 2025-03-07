package com.ecom.service.impl;

import com.ecom.model.Comment;
import com.ecom.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public Comment createComment(Comment comment) {
        comment.setCreatedAt(new Date());
        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsByReviewId(Integer reviewId) {
        return commentRepository.findByReviewId(reviewId);
    }
}
