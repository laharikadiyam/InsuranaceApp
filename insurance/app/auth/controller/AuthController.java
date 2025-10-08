package com.insurance.app.auth.controller;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import org.springframework.web.bind.annotation.*;


import com.insurance.app.auth.entity.Roles;
import com.insurance.app.auth.service.AdminService;
import com.insurance.app.auth.service.UserService;
import com.insurance.app.auth.dto.ChangePasswordRequest;
import com.insurance.app.auth.dto.ForgotPasswordRequest;
import com.insurance.app.auth.dto.LoginRequest;
import com.insurance.app.auth.dto.LoginResponse;
import com.insurance.app.auth.dto.RegistrationRequest;
import com.insurance.app.auth.exceptionHandling.AccessForbiddenException;
import com.insurance.app.auth.exceptionHandling.UserNotFoundException;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(
	    origins = "http://localhost:4200",
	    allowCredentials = "true"
	)
public class AuthController {

    private final UserService userService;
    private final AdminService adminService;

    public AuthController(UserService userService, AdminService adminService) {
        this.userService = userService;
        this.adminService = adminService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegistrationRequest req) {
        
    	String status="";
        if (req.getRole() == Roles.CUSTOMER) {
            status = userService.register(req);  
        } else {
        	status = adminService.register(req); 
        }
        return ResponseEntity.ok(status);
    }

    @PostMapping("/login/customer")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginRequest req) throws AccessForbiddenException {
        return ResponseEntity.ok(userService.loginUser(req));
    }

    @PostMapping("/login/admin")
    public ResponseEntity<LoginResponse> loginAdmin(@RequestBody LoginRequest req) throws AccessForbiddenException {
        return ResponseEntity.ok(adminService.loginAdmin(req));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest req) {
        
        try {
            userService.forgotPassword(req);
        } catch (UserNotFoundException e) {
            adminService.forgotPassword(req);
        }
        
        
        return ResponseEntity.ok("Password reset successfully");
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest req) {
        
        if (userService.existsByEmail(req.getEmail())) {
            userService.changePassword(req);
        } else {
            adminService.changePassword(req);
        }
        return ResponseEntity.ok("Password changed successfully");
    }
}