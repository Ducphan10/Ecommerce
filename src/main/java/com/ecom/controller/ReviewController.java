package com.ecom.controller;


import com.ecom.dto.request.RatingRequest;
import com.ecom.model.Product;
import com.ecom.model.ProductOrder;
import com.ecom.model.Rating;
import com.ecom.model.UserDtls;
import com.ecom.service.*;
import com.ecom.util.OrderStatus;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("")
public class ReviewController {
    @Autowired
    private ProductService productService;

    @Autowired
    private final IRatingService ratingService;
    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;
    @Autowired
    private CartService cartService;

    @GetMapping("/user/review")
    public String productDetail(Model model, @RequestParam Integer productId, @RequestParam Integer orderId, Principal p) {
        UserDtls user = getLoggedInUserDetails(p);
        if (user == null) {
            return "redirect:/login";
        }
        Integer userId = user.getId();

        Product product = productService.getProductById(productId);
        if (product == null) {
            return "redirect:/product-list";
        }

        List<Rating> ratings = ratingService.findByProductId(productId);
        if (ratings != null && !ratings.isEmpty()) {
            ratings.sort(Comparator.comparing(Rating::getId, Comparator.reverseOrder()));
        } else {
            ratings = Collections.emptyList();
        }
        int ratingCount = ratingService.countRatingStar(productId);

        int ratingUser = ratingService.countUser(productId);

        boolean hasOrdered = ratingService.checkOrderFirst(productId, userId);

        Integer countCart = cartService.getCountCart(user.getId());

        model.addAttribute("countCart", countCart);
        model.addAttribute("product", product);
        model.addAttribute("ratings", ratings);
        model.addAttribute("ratingCount", ratingCount);
        model.addAttribute("ratingUser", ratingUser);
        model.addAttribute("orderId", orderId);
        model.addAttribute("rating", new RatingRequest());
        model.addAttribute("hasOrdered", hasOrdered);
        model.addAttribute("user", user);

        return "user/review";  // Tệp user/review.html trong thư mục templates
    }
    private UserDtls getLoggedInUserDetails(Principal p) {
        String email = p.getName();
        return userService.getUserByEmail(email);
    }



@PostMapping("/user/reviews/{productId}/{orderId}")
public String reviews(@Valid @ModelAttribute("rating") RatingRequest ratingRequest,
                      @PathVariable Integer productId, Principal p, Model model,
                      @PathVariable Integer orderId,
                      RedirectAttributes redirectAttributes) {
    if (!SecurityContextHolder.getContext().getAuthentication().getName().equals("anonymousUser")) {
        UserDtls user = getLoggedInUserDetails(p);
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        ratingRequest.setProductId(productId);
        ratingRequest.setUserId(user.getId());
        ratingRequest.setOrderId(orderId);
        ProductOrder order = orderService.findById(orderId).get();

        if (ratingService.checkOrderFirst(productId, user.getId())) {
            if (!ratingService.insert(ratingRequest)) {
                String msg = "Not found user/product";
                redirectAttributes.addFlashAttribute("msg", msg);
                return "redirect:/user/reviews/product-detail/" + productId + "/" + orderId;
            }
        } else {
            String msg = "You need to buy first";
            redirectAttributes.addFlashAttribute("msg", msg);
            return "redirect:/user/reviews/product-detail/" + productId + "/" + orderId;
        }
    }
    return "redirect:/product/" + productId;

}





}
