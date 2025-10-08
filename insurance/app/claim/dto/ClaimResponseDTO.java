package com.insurance.app.claim.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimResponseDTO {
    private Long claimId;
    private Long userId;
    private Long purchaseId;
    private String claimStatus;
    private LocalDateTime uploadedAt;
    private String userName;
    private String userEmail;
}