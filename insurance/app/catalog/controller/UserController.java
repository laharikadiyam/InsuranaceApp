package com.insurance.app.catalog.controller;

import com.insurance.app.catalog.dto.PolicyDto;
import com.insurance.app.catalog.service.PolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("api/customer/availablepolicies")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
public class UserController {
    private final PolicyService policy_view_service;

    @GetMapping
    public ResponseEntity<List<PolicyDto>> getAll(@RequestParam(required = false) String type,
                                                  @RequestParam(required = false) Boolean activeOnly) {
        return ResponseEntity.ok(policy_view_service.getAllPolicies(type, activeOnly));
    }
}
