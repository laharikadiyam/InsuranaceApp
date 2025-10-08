package com.insurance.app.management.entity;

import com.insurance.app.auth.entity.Users;
import com.insurance.app.purchase.entity.PolicyPurchase;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthInsurance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int age;
    private int numberOfMembers;
    private double sumInsured;
    private boolean smoker;
    private boolean preExisting;
    private double premium;
    private String status; // PENDING, CONFIRMED, CANCELLED

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @OneToOne(cascade = CascadeType.ALL, optional = true)
    @JoinColumn(name = "purchase_id")
    @JsonIgnore
    private PolicyPurchase purchase;
}
