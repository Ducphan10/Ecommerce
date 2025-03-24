package com.ecom.dto.request;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class RatingRequest {
    @Column(nullable = false, length = 65535)
    private String content;

    @Column(nullable = false)
    private int star;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private Integer userId;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id", nullable = false)
    private Integer productId;

    @ManyToOne
    @MapsId("orderId")
    @JoinColumn(name = "order_id", nullable = false)
    private Integer orderId;
}
