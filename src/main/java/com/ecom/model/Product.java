package com.ecom.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(length = 500)
	private String title;

	@Column(length = 5000)
	private String description;

	private String category;

	private Double price;

	private int stock;

	private String image;

	private int discount;
	
	private Double discountPrice;
	
	private Boolean isActive;

	private String formattedDiscountPrice; // Thêm trường này

	private String formattedPrice;

	@OneToMany(mappedBy = "product")
	private List<Rating> ratings;


//	// Getter và Setter
//	public String getFormattedDiscountPrice() {
//		return formattedDiscountPrice;
//	}
//
//	public void setFormattedDiscountPrice(String formattedDiscountPrice) {
//		this.formattedDiscountPrice = formattedDiscountPrice;
//	}
//
//	public String getFormattedPrice() {
//		return formattedPrice;
//	}
//
//	public void setFormattedPrice(String formattedPrice) {
//		this.formattedPrice = formattedPrice;
//	}


}
