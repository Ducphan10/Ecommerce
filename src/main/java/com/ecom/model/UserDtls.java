package com.ecom.model;

import java.util.Date;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class UserDtls {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String name;

	private String mobileNumber;

	private String email;

	private String address;

	private String city;

	private String password;

	private String profileImage;

	private String role;

	private Boolean isEnable;

	private Boolean accountNonLocked;

	private Integer failedAttempt;

	private Date lockTime;

	private String resetToken;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private List<Rating> ratings;

}
