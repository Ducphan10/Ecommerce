package com.ecom.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Setter
@Getter
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Khóa chính mới

    @Column(nullable = false, length = 65535)
    private String content;

    @Column(nullable = false)
    private int star;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserDtls user;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private ProductOrder order;
}