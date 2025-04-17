package com.ecom.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class RevenueStatsDTO {
    private LocalDate date;
    private Double totalRevenue;
    private Long totalOrders;
    private Long totalProducts;

    public RevenueStatsDTO(LocalDate date, Double totalRevenue, Long totalOrders, Long totalProducts) {
        this.date = date;
        this.totalRevenue = totalRevenue;
        this.totalOrders = totalOrders;
        this.totalProducts = totalProducts;
    }
} 