package com.ecom.repository;

import com.ecom.model.Rating;
import com.ecom.model.composites.RatingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, RatingId> {
    List<Rating> findByProductId(Integer productId);
}