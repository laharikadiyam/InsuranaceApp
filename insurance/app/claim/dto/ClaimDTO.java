package com.insurance.app.claim.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimDTO {

    private Long claimId; // Auto-generated, no validation

    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    private Long userId;

    @NotNull(message = "Purchase ID is required")
    @Positive(message = "Purchase ID must be positive")
    private Long purchaseId;

    @NotBlank(message = "Claim status is required")
    @Size(max = 50, message = "Claim status must not exceed 50 characters")
    private String claimStatus;

    private LocalDateTime uploadedAt; // Auto-set in service, no validation
}
