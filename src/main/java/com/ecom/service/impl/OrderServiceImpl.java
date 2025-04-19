package com.ecom.service.impl;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ecom.model.Cart;
import com.ecom.model.OrderAddress;
import com.ecom.model.OrderRequest;
import com.ecom.model.ProductOrder;
import com.ecom.repository.CartRepository;
import com.ecom.repository.ProductOrderRepository;
import com.ecom.service.OrderService;
import com.ecom.util.CommonUtil;
import com.ecom.util.OrderStatus;
import com.ecom.model.Product;
import com.ecom.repository.ProductRepository;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private ProductOrderRepository orderRepository;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private CommonUtil commonUtil;

	@Autowired
	private ProductRepository productRepository;

	@Override
	public void saveOrder(Integer userid, OrderRequest orderRequest) throws Exception {
		List<Cart> carts = cartRepository.findByUserId(userid);

		for (Cart cart : carts) {
			// Check if product is still in stock
			Product product = cart.getProduct();
			if (product.getStock() < cart.getQuantity()) {
				throw new RuntimeException("Sản phẩm " + product.getTitle() + " không đủ số lượng tồn kho. Số lượng còn lại: " + product.getStock());
			}

			ProductOrder order = new ProductOrder();

			order.setOrderId(UUID.randomUUID().toString());
			order.setOrderDate(LocalDate.now());

			order.setProduct(cart.getProduct());
			order.setPrice(cart.getProduct().getDiscountPrice());

			order.setQuantity(cart.getQuantity());
			order.setUser(cart.getUser());

			order.setStatus(OrderStatus.IN_PROGRESS.getName());
			order.setPaymentType(orderRequest.getPaymentType());

			OrderAddress address = new OrderAddress();
			address.setFirstName(orderRequest.getFirstName());
			address.setLastName(orderRequest.getLastName());
			address.setEmail(orderRequest.getEmail());
			address.setMobileNo(orderRequest.getMobileNo());
			address.setAddress(orderRequest.getAddress());
			address.setCity(orderRequest.getCity());
			address.setState(orderRequest.getState());
			address.setPincode(orderRequest.getPincode());

			order.setOrderAddress(address);

			// Update product stock immediately
			product.setStock(product.getStock() - cart.getQuantity());
			productRepository.save(product);

			ProductOrder saveOrder = orderRepository.save(order);
			commonUtil.sendMailForProductOrder(saveOrder, "success");
		}
	}

	@Override
	public List<ProductOrder> getOrdersByUser(Integer userId) {
		List<ProductOrder> orders = orderRepository.findByUserId(userId);
		return orders;
	}

	@Override
	public ProductOrder updateOrderStatus(Integer id, String status) {
		Optional<ProductOrder> findById = orderRepository.findById(id);
		if (findById.isPresent()) {
			ProductOrder productOrder = findById.get();
			productOrder.setStatus(status);
			
			// Decrease product stock when order is delivered
			if (status.equals("Delivered")) {
				Product product = productOrder.getProduct();
				int currentStock = product.getStock();
				int orderedQuantity = productOrder.getQuantity();
				product.setStock(currentStock - orderedQuantity);
				productRepository.save(product);
			}
			
			ProductOrder updateOrder = orderRepository.save(productOrder);
			return updateOrder;
		}
		return null;
	}

	@Override
	public List<ProductOrder> getAllOrders() {
		return orderRepository.findAll();
	}

	@Override
	public Page<ProductOrder> getAllOrdersPagination(Integer pageNo, Integer pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		return orderRepository.findAllByOrderByOrderDateDesc(pageable);
	}

	@Override
	public void deleteFailOrder() {

	}

	@Override
	public Optional<ProductOrder> findById(Integer aLong) {
		return  orderRepository.findById(aLong);
	}

	@Override
	public ProductOrder getOrdersByOrderId(String orderId) {
		return orderRepository.findByOrderId(orderId);
	}


	public List<ProductOrder> findOrdersByUserId(Integer userId) {
		List<ProductOrder> orders = orderRepository.findByUserId(userId);
		// Filter out orders with inactive products
		return orders.stream()
				.filter(order -> order.getProduct().getIsActive())
				.collect(Collectors.toList());
	}

}
