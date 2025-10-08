package com.insurance.app.auth.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.*;

import com.insurance.app.auth.entity.Roles;
import com.insurance.app.auth.service.AdminService;
import com.insurance.app.auth.dto.UserProfileResponse;


@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(
	    origins = "http://localhost:4200",
	    allowCredentials = "true"
	)
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile() {
    	String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(adminService.loadUserProfile(email));
    }

    @GetMapping("/all-users")
    public ResponseEntity<List<UserProfileResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/pending-admins")
    public ResponseEntity<List<UserProfileResponse>> getPendingAdmins() {
        return ResponseEntity.ok(adminService.listInactiveUsersByRole(Roles.ADMIN));
    }

    @GetMapping("/pending-customers")
    public ResponseEntity<List<UserProfileResponse>> getPendingCustomers() {
        return ResponseEntity.ok(adminService.listInactiveUsersByRole(Roles.CUSTOMER));
    }

    @PostMapping("/activate-admin/{id}")
    public ResponseEntity<String> activateAdmin(@PathVariable Long id) {
    	String status=adminService.activateAdmin(id);
        return ResponseEntity.ok(status);
        
    }
    @PostMapping("/activate-customer/{id}")
    public ResponseEntity<String> activateCustomer(@PathVariable Long id) {
    	String status=adminService.activateCustomer(id);
        return ResponseEntity.ok(status);
    }

    @PostMapping("/deactivate-user/{id}")
    public ResponseEntity<String> deactivateUser(@PathVariable Long id) {
    	String status=adminService.deactivateUser(id);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/find-user")
    public ResponseEntity<UserProfileResponse> findUser(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Long id) {
        return ResponseEntity.ok(adminService.findUserByEmailOrId(email, id));
    }
}