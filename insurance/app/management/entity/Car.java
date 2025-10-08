package com.insurance.app.management.entity;

import com.insurance.app.auth.entity.Users;
import com.insurance.app.purchase.entity.PolicyPurchase;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Car {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long vehicle_id;

	private int cc;
	private int ageinMonths;
	private double idv;
	private String manufacturerName;
	private String registrationNumber;
	private double thirdPartyPremium;
	private double comprehensivePremium;

	private String status; // PENDING, CONFIRMED ,CANCELLED

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private Users user;

	@OneToOne(cascade = CascadeType.ALL, optional = true)
	@JoinColumn(name = "purchase_id")
	@JsonIgnore
	private PolicyPurchase purchase;
}
