package com.insurance.app.management.DTO;

import lombok.*;

@Data
@AllArgsConstructor
public class InsurancePremiumResponse {
	private double annualPremium;
    private double monthlyPremium;
}