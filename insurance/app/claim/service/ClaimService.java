package com.insurance.app.claim.service;

import com.insurance.app.claim.dto.ClaimDTO;
import com.insurance.app.claim.entity.Claim;
import com.insurance.app.claim.exception.ClaimNotFoundException;
import com.insurance.app.claim.repository.ClaimRepository;
import com.insurance.app.auth.entity.Users;
import com.insurance.app.auth.repository.UserRepository;
import com.insurance.app.catalog.exception.ResourceNotFoundException;
import com.insurance.app.purchase.entity.PolicyPurchase;
import com.insurance.app.purchase.repository.PolicyPurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final UserRepository userRepository;
    private final PolicyPurchaseRepository purchaseRepository;
    private final MessageSource messageSource;

    /**
     * Create a new claim
     */
    public Claim raiseClaim(ClaimDTO claimDTO) {
        // Validate user exists
        Users user = userRepository.findById(claimDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageSource.getMessage("user.notfound", new Object[]{claimDTO.getUserId()}, Locale.getDefault())
                ));

        // Validate purchase exists
        PolicyPurchase purchase = purchaseRepository.findById(claimDTO.getPurchaseId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageSource.getMessage("purchase.notfound", new Object[]{claimDTO.getPurchaseId()}, Locale.getDefault())
                ));

        // Validate that the purchase belongs to the user
        if (!purchase.getUser().getId().equals(claimDTO.getUserId())) {
            throw new RuntimeException(messageSource.getMessage("claim.purchase.user.mismatch", null, Locale.getDefault()));
        }

        // Validate that the policy is active
        if (!"CONFIRMED".equals(purchase.getStatus()) && !"ACTIVE".equals(purchase.getStatus())) {
            throw new RuntimeException(
                    messageSource.getMessage("claim.policy.notactive", new Object[]{purchase.getStatus()}, Locale.getDefault())
            );
        }

        // Check if a claim already exists for this purchase
        if (claimRepository.existsByPurchase_PurchaseId(claimDTO.getPurchaseId())) {
            throw new RuntimeException(messageSource.getMessage("claim.already.exists", null, Locale.getDefault()));
        }

        // Create new claim
        Claim claim = Claim.builder()
                .claimStatus("PENDING") // Always start with PENDING status
                .user(user)
                .purchase(purchase)
                .uploadedAt(LocalDateTime.now())
                .build();

        return claimRepository.save(claim);
    }

    /**
     * Get all claims (for admin)
     */
    public List<Claim> getAllClaims() {
        return claimRepository.findAll();
    }

    /**
     * Get claims by user ID
     */
    public List<Claim> getClaimsByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    messageSource.getMessage("user.notfound", new Object[]{userId}, Locale.getDefault())
            );
        }
        return claimRepository.findByUser_Id(userId);
    }

    /**
     * Get claim by ID
     */
    public Claim getClaimById(Long id) {
        return claimRepository.findById(id)
                .orElseThrow(() -> new ClaimNotFoundException(
                        messageSource.getMessage("claim.notfound", new Object[]{id}, Locale.getDefault())
                ));
    }

    /**
     * Update claim status (for admin)
     */
    public Claim updateClaimStatus(Long id, String status) {
        Claim claim = getClaimById(id);

        // Validate status
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException(
                    messageSource.getMessage("claim.status.invalid", new Object[]{status}, Locale.getDefault())
            );
        }

        // Check if claim is already in final state
        if ("APPROVED".equals(claim.getClaimStatus()) || "REJECTED".equals(claim.getClaimStatus())) {
            throw new RuntimeException(
                    messageSource.getMessage("claim.status.final", new Object[]{claim.getClaimStatus()}, Locale.getDefault())
            );
        }

        claim.setClaimStatus(status);
        return claimRepository.save(claim);
    }

    /**
     * Delete claim (withdraw by customer)
     */
    public void deleteClaim(Long id) {
        Claim claim = getClaimById(id);

        if (!"PENDING".equals(claim.getClaimStatus())) {
            throw new RuntimeException(
                    messageSource.getMessage("claim.delete.notpending", new Object[]{claim.getClaimStatus()}, Locale.getDefault())
            );
        }

        claimRepository.delete(claim);
    }

    /**
     * Get claims by status
     */
    public List<Claim> getClaimsByStatus(String status) {
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException(
                    messageSource.getMessage("claim.status.invalid", new Object[]{status}, Locale.getDefault())
            );
        }
        return claimRepository.findByClaimStatus(status);
    }

    /**
     * Get pending claims count
     */
    public long getPendingClaimsCount() {
        return claimRepository.countByClaimStatus("PENDING");
    }

    /**
     * Get claims count by user
     */
    public long getClaimsCountByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    messageSource.getMessage("user.notfound", new Object[]{userId}, Locale.getDefault())
            );
        }
        return claimRepository.countByUser_Id(userId);
    }

    private boolean isValidStatus(String status) {
        return status != null &&
                (status.equals("PENDING") || status.equals("APPROVED") || status.equals("REJECTED"));
    }

    /**
     * Check if user has any claims
     */
    public boolean hasUserClaims(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    messageSource.getMessage("user.notfound", new Object[]{userId}, Locale.getDefault())
            );
        }
        return claimRepository.existsByUser_Id(userId);
    }

    /**
     * Get claims for a specific purchase
     */
    public List<Claim> getClaimsByPurchase(Long purchaseId) {
        if (!purchaseRepository.existsById(purchaseId)) {
            throw new ResourceNotFoundException(
                    messageSource.getMessage("purchase.notfound", new Object[]{purchaseId}, Locale.getDefault())
            );
        }
        return claimRepository.findByPurchase_PurchaseId(purchaseId);
    }

    /**
     * Update claim (only pending)
     */
    public Claim updateClaim(Long id, ClaimDTO claimDTO) {
        Claim existingClaim = getClaimById(id);

        if (!"PENDING".equals(existingClaim.getClaimStatus())) {
            throw new RuntimeException(
                    messageSource.getMessage("claim.update.notpending", new Object[]{existingClaim.getClaimStatus()}, Locale.getDefault())
            );
        }

        if (claimDTO.getClaimStatus() != null && isValidStatus(claimDTO.getClaimStatus())) {
            existingClaim.setClaimStatus(claimDTO.getClaimStatus());
        }

        return claimRepository.save(existingClaim);
    }

    public List<PolicyPurchase> getActivePurchasesForClaims(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    messageSource.getMessage("user.notfound", new Object[]{userId}, Locale.getDefault())
            );
        }

        return purchaseRepository.findByUser_IdAndStatusIn(
                userId,
                Arrays.asList("CONFIRMED", "ACTIVE")
        );
    }
}
