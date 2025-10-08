package com.insurance.app.management.DTO;

import lombok.Data;

@Data
public class BikeDTO {
	private Long vehicle_id;
    private int cc;
    private int ageinMonths;
    private double idv;
    private String manufacturerName;
    private String registrationNumber;
    private double thirdPartyPremium;
    private double comprehensivePremium;
    private Long userid;
    private Long purchaseId;
    private String status;
}
