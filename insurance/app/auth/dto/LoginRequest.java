package com.insurance.app.auth.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginRequest {
	@NotNull(message="Email cannot be Null")
	@Pattern(
	        regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$",
	        message = "Invalid email format"
	    )
	private String email;
	@NotNull(message="Password cannot be Null")
    private String password;
}
