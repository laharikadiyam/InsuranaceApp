package com.insurance.app.management.DTO;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class LifeInsuranceDTO {
	private Long id;
	private int age;
    private String gender;
    private double sumAssured;
    private int policyTerm;
    private boolean smoker;
    private String occupationRisk;
    private double premium;
    private Long user_id;

}
