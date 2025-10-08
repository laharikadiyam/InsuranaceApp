package com.insurance.app.management.DTO;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthInsuranceDTO {
		private Long id;
	  	private int age;
	    private int numberOfMembers;
	    private double sumInsured;
	    private boolean smoker;
	    private boolean preExisting;
	    private double premium;
	    private Long user_id;
	    

}
