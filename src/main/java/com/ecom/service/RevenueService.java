package com.ecom.service;

import com.ecom.dto.RevenueStatsDTO;
import com.ecom.repository.ProductOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

@Service
public class RevenueService {

    private static final Logger logger = LoggerFactory.getLogger(RevenueService.class);

    @Autowired
    private ProductOrderRepository productOrderRepository;

    public List<RevenueStatsDTO> getRevenueStats(LocalDate startDate, LocalDate endDate) {
        try {
            logger.info("Fetching revenue stats from repository");
            List<RevenueStatsDTO> stats = productOrderRepository.getRevenueStatsByDateRange(startDate, endDate);
            logger.info("Retrieved {} revenue stats records", stats.size());
            return stats;
        } catch (Exception e) {
            logger.error("Error in getRevenueStats", e);
            throw e;
        }
    }

    public List<RevenueStatsDTO> getDeliveredRevenueStats(LocalDate startDate, LocalDate endDate) {
        try {
            logger.info("Fetching delivered revenue stats from repository");
            List<RevenueStatsDTO> stats = productOrderRepository.getDeliveredRevenueStatsByDateRange(startDate, endDate);
            logger.info("Retrieved {} delivered revenue stats records", stats.size());
            return stats;
        } catch (Exception e) {
            logger.error("Error in getDeliveredRevenueStats", e);
            throw e;
        }
    }

    public Double getTotalRevenue(LocalDate startDate, LocalDate endDate) {
        try {
            logger.info("Calculating total revenue");
            List<RevenueStatsDTO> stats = getDeliveredRevenueStats(startDate, endDate);
            Double total = stats.stream()
                    .mapToDouble(RevenueStatsDTO::getTotalRevenue)
                    .sum();
            logger.info("Total revenue calculated: {}", total);
            return total;
        } catch (Exception e) {
            logger.error("Error in getTotalRevenue", e);
            throw e;
        }
    }
} 