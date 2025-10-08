package com.insurance.app.claim.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.insurance.app.purchase.entity.PolicyPurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.insurance.app.claim.entity.Claim;

public interface ClaimRepository extends JpaRepository<Claim, Long> {

    // Find claims by user ID
    List<Claim> findByUser_Id(Long userId);

    // Find claims by status
    List<Claim> findByClaimStatus(String status);

    // Find claims by purchase ID
    List<Claim> findByPurchase_PurchaseId(Long purchaseId);

    // Check if claim exists for purchase
    boolean existsByPurchase_PurchaseId(Long purchaseId);

    // Count claims by status
    long countByClaimStatus(String status);

    // Count claims by user
    long countByUser_Id(Long userId);

    // Check if user has claims
    boolean existsByUser_Id(Long userId);

    // Find claims by user and status
    List<Claim> findByUser_IdAndClaimStatus(Long userId, String status);

    // Find recent claims (last 30 days)
    @Query("SELECT c FROM Claim c WHERE c.uploadedAt >= :startDate ORDER BY c.uploadedAt DESC")
    List<Claim> findRecentClaims(@Param("startDate") LocalDateTime startDate);

}