package com.insurance.app.purchase.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.insurance.app.management.entity.Bike;
import com.insurance.app.management.entity.Car;
import com.insurance.app.management.entity.HealthInsurance;
import com.insurance.app.management.entity.LifeInsurance;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

import com.insurance.app.auth.entity.Users;
import com.insurance.app.catalog.entity.Policy;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "policy_purchases")
public class PolicyPurchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long purchaseId;

    private LocalDate purchaseDate;
    private LocalDate expiryDate;
    private String status ="ACTIVE";  // ACTIVE , CANCELLED ,EXPIRED

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"password", "authorities", "policyPurchases"}) // adjust to your Users fields
    private Users user;

    @ManyToOne
    @JoinColumn(name = "bike_policy_id")
    @JsonIgnoreProperties({"purchase", "user"})
    private Bike bikePolicy;

    @ManyToOne
    @JoinColumn(name = "car_policy_id")
    @JsonIgnoreProperties({"purchase", "user"})
    private Car carPolicy;

    @ManyToOne
    @JoinColumn(name = "health_policy_id")
    @JsonIgnoreProperties({"purchase", "user"})
    private HealthInsurance healthPolicy;

    @ManyToOne
    @JoinColumn(name = "life_policy_id")
    @JsonIgnoreProperties({"purchase", "user"})
    private LifeInsurance lifePolicy;
}
