package com.insurance.app.purchase.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PolicyPurchaseRequest {
    private Long userId;
    private Long bikePolicyId;
    private Long carPolicyId;
    private Long healthPolicyId;
    private Long lifePolicyId;
    private LocalDate purchaseDate;
    private LocalDate expiryDate;
}
