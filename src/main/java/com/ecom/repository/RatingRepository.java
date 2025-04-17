package com.ecom.repository;

import com.ecom.model.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByProductId(Integer productId);
    
    @Query("SELECT r FROM Rating r WHERE LOWER(r.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Rating> searchByContent(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT r FROM Rating r WHERE LOWER(r.product.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Rating> searchByProductName(@Param("keyword") String keyword, Pageable pageable);
}