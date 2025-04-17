package com.ecom.controller;

import com.ecom.model.Rating;
import com.ecom.service.IRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminRatingController {

    @Autowired
    private IRatingService ratingService;

    @GetMapping("/ratings")
    public String getAllRatings(Model model,
                              @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                              @RequestParam(name = "sort", defaultValue = "newest") String sort,
                              @RequestParam(name = "keyword", required = false) String keyword,
                              @RequestParam(name = "searchType", defaultValue = "content") String searchType) {
        
        Sort.Direction direction = Sort.Direction.DESC;
        String sortBy = "id";
        
        switch (sort) {
            case "oldest":
                direction = Sort.Direction.ASC;
                break;
            case "highest":
                sortBy = "star";
                break;
            case "lowest":
                sortBy = "star";
                direction = Sort.Direction.ASC;
                break;
            default:
                break;
        }
        
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(direction, sortBy));
        Page<Rating> page;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            if ("product".equals(searchType)) {
                page = ratingService.searchByProductName(keyword, pageable);
            } else {
                page = ratingService.searchByContent(keyword, pageable);
            }
        } else {
            page = ratingService.findAll(pageable);
        }
        
        model.addAttribute("ratings", page.getContent());
        model.addAttribute("pageNo", page.getNumber());
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalElements", page.getTotalElements());
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("isFirst", page.isFirst());
        model.addAttribute("isLast", page.isLast());
        model.addAttribute("currentSort", sort);
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchType", searchType);
        
        return "admin/ratings";
    }

    @GetMapping("/delete-rating/{id}")
    public String deleteRating(@PathVariable("id") Long id,
                             RedirectAttributes redirectAttributes) {
        try {
            ratingService.deleteById(id);
            redirectAttributes.addFlashAttribute("succMsg", "Xóa bình luận thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi khi xóa bình luận: " + e.getMessage());
        }
        return "redirect:/admin/ratings";
    }
} 