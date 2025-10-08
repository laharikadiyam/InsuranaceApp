package com.insurance.app.claim.entity;

import java.time.LocalDateTime;

import com.insurance.app.auth.entity.Users;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long documentId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne
    @JoinColumn(name = "claim_id", nullable = false)
    private Claim claim;

    private String documentType;
    private String fileUrl;

    private LocalDateTime uploadedAt;
    private boolean verified;

    @PrePersist
    public void setUploadedAtNow() {
        this.uploadedAt = LocalDateTime.now();
    }
}