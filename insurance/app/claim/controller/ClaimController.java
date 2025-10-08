package com.insurance.app.claim.controller;

import com.insurance.app.claim.dto.ClaimDTO;
import com.insurance.app.claim.entity.Claim;
import com.insurance.app.claim.service.ClaimService;

import com.insurance.app.purchase.entity.PolicyPurchase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins="*")
@RestController
@RequestMapping("/api/customer/claims")
@PreAuthorize("hasRole('CUSTOMER')")
@RequiredArgsConstructor
public class ClaimController {
    private final ClaimService claimService;

    @PostMapping
    public ResponseEntity<?> raiseClaim(@Valid @RequestBody ClaimDTO claim) {
        try {
            Claim createdClaim = claimService.raiseClaim(claim);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdClaim);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create claim: " + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getClaimsByUser(@PathVariable Long userId) {
        try {
            List<Claim> claims = claimService.getClaimsByUser(userId);
            return ResponseEntity.ok(claims);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to get claims: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getClaimById(@PathVariable Long id) {
        try {
            Claim claim = claimService.getClaimById(id);
            return ResponseEntity.ok(claim);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Claim not found: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteClaim(@PathVariable Long id) {
        try {
            claimService.deleteClaim(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete claim: " + e.getMessage());
        }
    }
    @GetMapping("/active-policies/{userId}")
    public ResponseEntity<?> getActivePoliciesForClaims(@PathVariable Long userId) {
        try {
            List<PolicyPurchase> activePolicies = claimService.getActivePurchasesForClaims(userId);
            return ResponseEntity.ok(activePolicies);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to get active policies: " + e.getMessage());
        }
    }
}