package com.ecom.service.impl;

import com.ecom.dto.request.RatingRequest;
import com.ecom.model.Product;
import com.ecom.model.ProductOrder;
import com.ecom.model.Rating;
import com.ecom.model.UserDtls;
import com.ecom.repository.ProductOrderRepository;
import com.ecom.repository.ProductRepository;
import com.ecom.repository.RatingRepository;
import com.ecom.repository.UserRepository;
import com.ecom.service.IRatingService;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements IRatingService {
    private final RatingRepository ratingRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductOrderRepository orderRepository;

    @Override
    public List<Rating> findByProductId(Integer productId) {
        return ratingRepository.findByProductId(productId);
    }

    @Override
    public int countUser(Integer productId) {
        return ratingRepository.findByProductId(productId).size();
    }

    @Override
    public int countRatingStar(Integer productId) {
        List<Rating> ratings = ratingRepository.findByProductId(productId);
        if (ratings.isEmpty())
            return 0;
        int sum = 0;
        for (Rating rating : ratings) {
            sum += rating.getStar();
        }
        return sum / ratings.size();
    }

    @Override
    public List<Rating> findAll() {
        return ratingRepository.findAll();
    }

    @Override
    public Page<Rating> findAll(Pageable pageable) {
        return ratingRepository.findAll(pageable);
    }

    @Override
    public <S extends Rating> S save(S entity) {
        return ratingRepository.save(entity);
    }

    @Override
    public Optional<Rating> findById(Long id) {
        return ratingRepository.findById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return ratingRepository.existsById(id);
    }

    @Override
    public long count() {
        return ratingRepository.count();
    }

    @Override
    public void deleteById(Long id) {
        ratingRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        ratingRepository.deleteAll();
    }

    @Override
    public boolean insert(RatingRequest ratingRequest) {
        try {
            Product product = productRepository.findById(ratingRequest.getProductId()).orElse(null);
            UserDtls user = userRepository.findById(ratingRequest.getUserId()).orElse(null);
            ProductOrder order = orderRepository.findById(ratingRequest.getOrderId()).orElse(null);
            if (product == null || user == null || order == null) {
                return false;
            }
            Rating rating = Rating.builder()
                    .content(ratingRequest.getContent())
                    .star(ratingRequest.getStar())
                    .user(user)
                    .product(product)
                    .order(order)
                    .build();
            ratingRepository.save(rating);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean checkOrderFirst(Integer productId, Integer userId) {
        List<ProductOrder> orders = orderRepository.findByProductId(productId);

        if (orders.size() > 0) {
            for (ProductOrder order : orders) {
                if (order.getUser().getId().equals(userId) && "Delivered".equals(order.getStatus())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Page<Rating> searchByContent(String keyword, Pageable pageable) {
        return ratingRepository.searchByContent(keyword, pageable);
    }

    @Override
    public Page<Rating> searchByProductName(String keyword, Pageable pageable) {
        return ratingRepository.searchByProductName(keyword, pageable);
    }
}

