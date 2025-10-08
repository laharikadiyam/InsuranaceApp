package com.insurance.app.claim.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.insurance.app.claim.dto.ClaimStatusUpdateDTO;
import com.insurance.app.claim.entity.Claim;
import com.insurance.app.claim.service.ClaimService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins="*")
@RestController
@RequestMapping("/api/admin/claims")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminClaimController {

    private final ClaimService claimService;

    @GetMapping
    public List<Claim> getAllClaims() {
        return claimService.getAllClaims();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody ClaimStatusUpdateDTO statusDto) {

        try {
            Claim updatedClaim = claimService.updateClaimStatus(id, statusDto.getStatus());
            return ResponseEntity.ok(updatedClaim);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update claim status: " + e.getMessage());
        }
    }
}