package com.insurance.app.management.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PremiumRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String insuranceType; // e.g., "Life Insurance", "Health Insurance"
    private double annualPremium;
    private double monthlyPremium;
    private String details; // extra info (age, sum assured, etc.)
}
