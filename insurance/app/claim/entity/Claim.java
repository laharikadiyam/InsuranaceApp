package com.insurance.app.claim.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.insurance.app.auth.entity.Users;
import com.insurance.app.purchase.entity.PolicyPurchase;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Claim {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long claimId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"claims", "purchases"})
    private Users user;

    @ManyToOne
    @JoinColumn(name = "purchase_id", nullable = false)
    @JsonIgnoreProperties({"claims", "user"})
    private PolicyPurchase purchase;

    private String claimStatus;
    private LocalDateTime uploadedAt;

    @PrePersist
    public void setUploadedAtNow()
    {
        this.uploadedAt = LocalDateTime.now();
    }
}
