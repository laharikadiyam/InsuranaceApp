package com.insurance.app.claim.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.insurance.app.auth.entity.Users;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne
    @JoinColumn(name = "claim_id")
    private Claim claim;

    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;

    @PrePersist
    public void setCreatedAtNow() {
        this.createdAt = LocalDateTime.now();
    }
}