package com.ecom.service;

import com.ecom.dto.request.RatingRequest;
import com.ecom.model.Rating;
import com.ecom.model.composites.RatingId;

import java.util.List;
import java.util.Optional;

public interface IRatingService {
    List<Rating> findByProductId(Integer productId);

    int countUser(Integer productId);

    int countRatingStar(Integer productId);

    List<Rating> findAll();

    <S extends Rating> S save(S entity);

    Optional<Rating> findById(RatingId ratingId);

    boolean existsById(RatingId ratingId);

    long count();

    void deleteById(RatingId ratingId);

    void deleteAll();

    boolean insert(RatingRequest ratingRequest);

    boolean checkOrderFirst(Integer productId, Integer userId);
}