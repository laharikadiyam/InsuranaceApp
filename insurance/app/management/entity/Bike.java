package com.insurance.app.management.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.insurance.app.auth.entity.Users;
import com.insurance.app.purchase.entity.PolicyPurchase;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@Entity
@Getter
@Setter
public class Bike {

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
    @JoinColumn(name="purchase_id")
    @JsonIgnore
    private PolicyPurchase purchase;
}

