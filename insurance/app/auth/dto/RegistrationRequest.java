package com.insurance.app.auth.dto;

import com.insurance.app.auth.entity.Roles;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
//lombok
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequest {
	@NotNull(message="Name cannot be Null")
	private String name;
	@NotNull(message="Email cannot be Null")
	@Pattern(
	        regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$",
	        message = "Invalid email format"
	    )
    private String email;
	@NotNull(message="Password cannot be Null")
    private String password;
	@NotNull(message="role cannot be Null")
    private Roles role;
	@NotNull(message="Pan cannot be Null")
	@Pattern(
	        regexp = "[A-Z]{5}[0-9]{4}[A-Z]{1}",
	        message = "Invalid PAN number format"
	    )
    private String panNumber;
}
