package com.ecom.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String content; // Nội dung bình luận

    private Date createdAt; // Ngày tạo bình luận

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserDtls user; // Người bình luận

    @ManyToOne
    @JoinColumn(name = "review_id")
    private Review review; // Đánh giá liên quan

}
