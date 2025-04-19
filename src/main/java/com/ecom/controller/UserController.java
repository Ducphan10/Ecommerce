package com.ecom.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

import com.ecom.model.*;
import com.ecom.service.impl.OrderServiceImpl;
import jakarta.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.repository.UserRepository;
import com.ecom.service.CartService;
import com.ecom.service.CategoryService;
import com.ecom.service.OrderService;
import com.ecom.service.UserService;
import com.ecom.util.CommonUtil;
import com.ecom.util.OrderStatus;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserService userService;
	@Autowired
	private CategoryService categoryService;

	@Autowired
	private CartService cartService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private CommonUtil commonUtil;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private OrderServiceImpl orderServiceImpl;


	@GetMapping("/")
	public String home() {
		return "user/home";
	}

	@ModelAttribute
	public void getUserDetails(Principal p, Model m) {
		if (p != null) {
			String email = p.getName();
			UserDtls userDtls = userService.getUserByEmail(email);
			m.addAttribute("user", userDtls);
			Integer countCart = cartService.getCountCart(userDtls.getId());
			m.addAttribute("countCart", countCart);
		}

		List<Category> allActiveCategory = categoryService.getAllActiveCategory();
		m.addAttribute("categorys", allActiveCategory);
	}

	@GetMapping("/addCart")
	public String addToCart(@RequestParam Integer pid, @RequestParam Integer uid, HttpSession session) {
		try {
			Cart saveCart = cartService.saveCart(pid, uid);
			if (ObjectUtils.isEmpty(saveCart)) {
				session.setAttribute("errorMsg", "Thêm sản phẩm vào giỏ hàng thất bại");
			} else {
				session.setAttribute("succMsg", "Đã thêm sản phẩm vào giỏ hàng");
			}
		} catch (RuntimeException e) {
			session.setAttribute("errorMsg", e.getMessage());
		}
		return "redirect:/product/" + pid;
	}

//	@GetMapping("/cart")
//	public String loadCartPage(Principal p, Model m) {
//
//		UserDtls user = getLoggedInUserDetails(p);
//		List<Cart> carts = cartService.getCartsByUser(user.getId());
//		m.addAttribute("carts", carts);
//		if (carts.size() > 0) {
//			Double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
//			m.addAttribute("totalOrderPrice", totalOrderPrice);
//		}
//		return "/user/cart";
//	}

	@GetMapping("/cart")
	public String loadCartPage(Principal p, Model m) {
		UserDtls user = getLoggedInUserDetails(p);
		List<Cart> carts = cartService.getCartsByUser(user.getId());
		m.addAttribute("carts", carts);

		// Định dạng giá cho từng sản phẩm trong giỏ hàng
		for (Cart cart : carts) {
			cart.setFormattedTotalPrice(formatCurrency(cart.getTotalPrice()));  // Định dạng tổng giá sản phẩm
			cart.getProduct().setFormattedDiscountPrice(formatCurrency(cart.getProduct().getDiscountPrice()));  // Định dạng giá sản phẩm
		}

		// Định dạng tổng giá trị đơn hàng
		if (carts.size() > 0) {
			Double totalOrderPrice = carts.stream()
					.mapToDouble(Cart::getTotalPrice)
					.sum();  // Tính tổng giá trị giỏ hàng
			String formattedTotalOrderPrice = formatCurrency(totalOrderPrice);  // Định dạng tổng giá trị giỏ hàng
			m.addAttribute("formattedTotalOrderPrice", formattedTotalOrderPrice);
		}

		return "/user/cart";
	}



	private String formatCurrency(double amount) {
		NumberFormat formatter = new DecimalFormat("#,###");
		return formatter.format(amount);
	}

	@GetMapping("/cartQuantityUpdate")
	public String updateCartQuantity(@RequestParam String sy, @RequestParam Integer cid, HttpSession session) {
		try {
			cartService.updateQuantity(sy, cid);
		} catch (RuntimeException e) {
			session.setAttribute("errorMsg", e.getMessage());
		}
		return "redirect:/user/cart";
	}

	private UserDtls getLoggedInUserDetails(Principal p) {
		String email = p.getName();
		UserDtls userDtls = userService.getUserByEmail(email);
		return userDtls;
	}

//	@GetMapping("/orders")
//	public String orderPage(Principal p, Model m) {
//		UserDtls user = getLoggedInUserDetails(p);
//		List<Cart> carts = cartService.getCartsByUser(user.getId());
//		m.addAttribute("carts", carts);
//		if (carts.size() > 0) {
//			Double orderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
//			Double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice() + 250 + 100;
//			m.addAttribute("orderPrice", orderPrice);
//			m.addAttribute("totalOrderPrice", totalOrderPrice);
//		}
//		return "/user/order";
//	}

	@GetMapping("/orders")
	public String orderPage(Principal p, Model m, HttpSession session) {
		UserDtls user = getLoggedInUserDetails(p);
		List<Cart> carts = cartService.getCartsByUser(user.getId());
		m.addAttribute("carts", carts);

		if (carts.size() > 0) {
			Double orderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
			Double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice()  ;

			String formattedOrderPrice = formatCurrency(orderPrice);
			String formattedTotalOrderPrice = formatCurrency(totalOrderPrice);


			m.addAttribute("orderPrice", formattedOrderPrice);
			m.addAttribute("totalOrderPrice", formattedTotalOrderPrice);
			m.addAttribute("totalPriceToPayment", totalOrderPrice);

			session.setAttribute("totalPriceToPayment", BigDecimal.valueOf(totalOrderPrice));
		}
		return "/user/order";
	}


	@GetMapping("/productOrders")
	public String getUserOrders(Principal p, Model model) {

		UserDtls user = getLoggedInUserDetails(p);

		if (user == null) {
			return "redirect:/login";
		}

		// Lấy thông tin các đơn hàng của người dùng từ database
		List<ProductOrder> orders = orderServiceImpl.findOrdersByUserId(user.getId());

		Set<String> productNames = new HashSet<>();
		List<ProductOrder> uniqueOrders = new ArrayList<>();

		for (ProductOrder order : orders) {
			String productName = order.getProduct().getTitle();
			if (!productNames.contains(productName)) {
				productNames.add(productName);
				uniqueOrders.add(order);
			}
		}

		// Định dạng giá trị tiền của từng đơn hàng
		for (ProductOrder order : uniqueOrders) {
			String formattedPrice = formatCurrency(order.getPrice());
			order.setFormattedPrice(formattedPrice);
		}
		// Thêm thông tin đơn hàng vào model để chuyển tới view
		model.addAttribute("orders", uniqueOrders);

		// Trả về tên của view (HTML file) chứa danh sách đơn hàng
		return "/user/product_orders";
	}




	@PostMapping("/save-order")
	public String saveOrder(@ModelAttribute OrderRequest request, Principal p, HttpSession session) throws Exception {
		try {
			UserDtls user = getLoggedInUserDetails(p);
			orderService.saveOrder(user.getId(), request);

			if(Objects.equals(request.getPaymentType(), "ONLINE")) {
				return "redirect:/user/checkout/vnpay";
			}

			return "redirect:/user/success";
		} catch (RuntimeException e) {
			session.setAttribute("errorMsg", e.getMessage());
			return "redirect:/user/orders";
		}
	}

	@GetMapping("/success")
	public String loadSuccess() {
		return "/user/success";
	}

//	@GetMapping("/user-orders")
//	public String myOrder(Model m, Principal p) {
//		UserDtls loginUser = getLoggedInUserDetails(p);
//		List<ProductOrder> orders = orderService.getOrdersByUser(loginUser.getId());
//
//
//
//
//		m.addAttribute("orders", orders);
//		return "/user/my_orders";
//	}

	@GetMapping("/user-orders")
	public String myOrder(Model m, Principal p) {
		UserDtls loginUser = getLoggedInUserDetails(p);
		List<ProductOrder> orders = orderService.getOrdersByUser(loginUser.getId());

		// Định dạng giá cho từng đơn hàng
		for (ProductOrder o : orders) {
			// Định dạng giá trị tiền
			String formattedPrice = formatCurrency(o.getPrice());
			String formattedTotalPrice = formatCurrency(o.getQuantity() * o.getPrice());

			// Thêm giá trị đã định dạng vào model
			o.setFormattedPrice(formattedPrice);
			o.setFormattedTotalPrice(formattedTotalPrice);
		}

		m.addAttribute("orders", orders);
		return "/user/my_orders";
	}


	@GetMapping("/update-status")
	public String updateOrderStatus(@RequestParam Integer id, @RequestParam Integer st, HttpSession session) {

		OrderStatus[] values = OrderStatus.values();
		String status = null;

		for (OrderStatus orderSt : values) {
			if (orderSt.getId().equals(st)) {
				status = orderSt.getName();
			}
		}

		ProductOrder updateOrder = orderService.updateOrderStatus(id, status);
		
		try {
			commonUtil.sendMailForProductOrder(updateOrder, status);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!ObjectUtils.isEmpty(updateOrder)) {
			session.setAttribute("succMsg", "Status Updated");
		} else {
			session.setAttribute("errorMsg", "status not updated");
		}
		return "redirect:/user/user-orders";
	}

	@GetMapping("/profile")
	public String profile() {
		return "/user/profile";
	}

	@PostMapping("/update-profile")
	public String updateProfile(@ModelAttribute UserDtls user, @RequestParam MultipartFile img, HttpSession session) {
		UserDtls updateUserProfile = userService.updateUserProfile(user, img);
		if (ObjectUtils.isEmpty(updateUserProfile)) {
			session.setAttribute("errorMsg", "Profile not updated");
		} else {
			session.setAttribute("succMsg", "Profile Updated");
		}
		return "redirect:/user/profile";
	}

	@PostMapping("/change-password")
	public String changePassword(@RequestParam String newPassword, @RequestParam String currentPassword, Principal p,
			HttpSession session) {
		UserDtls loggedInUserDetails = getLoggedInUserDetails(p);

		boolean matches = passwordEncoder.matches(currentPassword, loggedInUserDetails.getPassword());

		if (matches) {
			String encodePassword = passwordEncoder.encode(newPassword);
			loggedInUserDetails.setPassword(encodePassword);
			UserDtls updateUser = userService.updateUser(loggedInUserDetails);
			if (ObjectUtils.isEmpty(updateUser)) {
				session.setAttribute("errorMsg", "Password not updated !! Error in server");
			} else {
				session.setAttribute("succMsg", "Password Updated sucessfully");
			}
		} else {
			session.setAttribute("errorMsg", "Current Password incorrect");
		}

		return "redirect:/user/profile";
	}

	@GetMapping("/deleteCart")
	public String deleteCart(@RequestParam Integer cid) {
		cartService.deleteCart(cid);
		return "redirect:/user/cart";
	}
}
