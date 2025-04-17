package com.ecom.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ecom.model.ProductOrder;
import com.ecom.dto.RevenueStatsDTO;

import java.time.LocalDate;

public interface ProductOrderRepository extends JpaRepository<ProductOrder, Integer> {

	Logger logger = LoggerFactory.getLogger(ProductOrderRepository.class);

	List<ProductOrder> findByUserId(Integer userId);

	ProductOrder findByOrderId(String orderId);

	List<ProductOrder> findByProductId(Integer productId);

	Page<ProductOrder> findAllByOrderByOrderDateDesc(Pageable pageable);

	@Query("SELECT new com.ecom.dto.RevenueStatsDTO(o.orderDate, COALESCE(SUM(o.price * o.quantity), 0), " +
		   "CAST(COUNT(DISTINCT o.orderId) AS java.lang.Long), CAST(COALESCE(SUM(o.quantity), 0) AS java.lang.Long)) " +
		   "FROM ProductOrder o " +
		   "WHERE o.orderDate BETWEEN :startDate AND :endDate " +
		   "AND (o.status = 'Delivered' OR o.status = 'Success' OR o.status = 'DELIVERED' OR o.status = 'SUCCESS') " +
		   "GROUP BY o.orderDate " +
		   "ORDER BY o.orderDate")
	List<RevenueStatsDTO> getRevenueStatsByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

	@Query("SELECT new com.ecom.dto.RevenueStatsDTO(o.orderDate, COALESCE(SUM(o.price * o.quantity), 0), " +
		   "CAST(COUNT(DISTINCT o.orderId) AS java.lang.Long), CAST(COALESCE(SUM(o.quantity), 0) AS java.lang.Long)) " +
		   "FROM ProductOrder o " +
		   "WHERE o.orderDate BETWEEN :startDate AND :endDate " +
		   "AND (o.status = 'Delivered' OR o.status = 'Success' OR o.status = 'DELIVERED' OR o.status = 'SUCCESS') " +
		   "GROUP BY o.orderDate " +
		   "ORDER BY o.orderDate")
	List<RevenueStatsDTO> getDeliveredRevenueStatsByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}
