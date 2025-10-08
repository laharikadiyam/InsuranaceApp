package com.insurance.app.auth.dto;

import com.insurance.app.auth.entity.Roles;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginResponse {
    private Long id;
	private String name;
    private String email;
    private Roles role;
    private String token;
}
