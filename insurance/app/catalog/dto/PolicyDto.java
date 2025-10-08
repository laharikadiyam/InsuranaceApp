package com.insurance.app.catalog.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyDto {

    private Long policy_id;

    @NotBlank(message = "Policy name is required")
    @Size(max = 100, message = "Policy name must not exceed 100 characters")
    private String policyName;

    @NotBlank(message = "Type is required")
    @Size(max = 50, message = "Type must not exceed 50 characters")
    private String type;

    @Positive(message = "Premium must be greater than 0")
    private double premium;

    @NotNull(message = "Tenure (in months) is required")
    @Min(value = 1, message = "Tenure must be at least 6 months")
    @Max(value = 600, message = "Tenure must not exceed 600 months (50 years)")
    private Integer tenure;

    @NotBlank(message = "Coverage is required")
    @Size(max = 255, message = "Coverage must not exceed 255 characters")
    private String coverage;

    private boolean active;
}
