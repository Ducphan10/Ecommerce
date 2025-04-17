package com.ecom.service;

import com.ecom.dto.request.RatingRequest;
import com.ecom.model.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IRatingService {
    List<Rating> findByProductId(Integer productId);

    int countUser(Integer productId);

    int countRatingStar(Integer productId);

    List<Rating> findAll();

    Page<Rating> findAll(Pageable pageable);

    <S extends Rating> S save(S entity);

    Optional<Rating> findById(Long id);

    boolean existsById(Long id);

    long count();

    void deleteById(Long id);

    void deleteAll();

    boolean insert(RatingRequest ratingRequest);

    boolean checkOrderFirst(Integer productId, Integer userId);

    Page<Rating> searchByContent(String keyword, Pageable pageable);

    Page<Rating> searchByProductName(String keyword, Pageable pageable);
}