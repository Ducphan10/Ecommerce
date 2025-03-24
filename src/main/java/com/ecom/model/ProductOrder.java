package com.ecom.model;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class ProductOrder {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String orderId;

	private LocalDate orderDate;

	@ManyToOne
	private Product product;

	private Double price;

	private Integer quantity;

	@ManyToOne
	private UserDtls user;

	private String status;

	private String paymentType;

	private String formattedPrice;  // Định dạng giá tiền

	private String formattedTotalPrice;  // Định dạng tổng tiền

	@OneToOne(cascade = CascadeType.ALL)
	private OrderAddress orderAddress;

	@OneToMany(mappedBy = "order")
	private List<Rating> ratings;



}
