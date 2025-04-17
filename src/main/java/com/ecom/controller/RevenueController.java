package com.ecom.controller;

import com.ecom.dto.RevenueStatsDTO;
import com.ecom.service.RevenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/revenue")
public class RevenueController {

    private static final Logger logger = LoggerFactory.getLogger(RevenueController.class);

    @Autowired
    private RevenueService revenueService;

    @GetMapping("/stats")
    public ResponseEntity<?> getRevenueStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            logger.info("Getting revenue stats from {} to {}", startDate, endDate);
            List<RevenueStatsDTO> stats = revenueService.getRevenueStats(startDate, endDate);
            logger.info("Found {} records", stats.size());
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Error getting revenue stats", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error getting revenue stats: " + e.getMessage());
        }
    }

    @GetMapping("/delivered-stats")
    public ResponseEntity<?> getDeliveredRevenueStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            logger.info("Getting delivered revenue stats from {} to {}", startDate, endDate);
            List<RevenueStatsDTO> stats = revenueService.getDeliveredRevenueStats(startDate, endDate);
            logger.info("Found {} delivered records", stats.size());
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Error getting delivered revenue stats", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error getting delivered revenue stats: " + e.getMessage());
        }
    }

    @GetMapping("/total")
    public ResponseEntity<?> getTotalRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            logger.info("Getting total revenue from {} to {}", startDate, endDate);
            Double total = revenueService.getTotalRevenue(startDate, endDate);
            logger.info("Total revenue: {}", total);
            return ResponseEntity.ok(total);
        } catch (Exception e) {
            logger.error("Error getting total revenue", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error getting total revenue: " + e.getMessage());
        }
    }
} 