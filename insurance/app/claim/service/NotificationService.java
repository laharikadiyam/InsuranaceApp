package com.insurance.app.claim.service;

import com.insurance.app.claim.entity.Notification;
import com.insurance.app.claim.repository.NotificationRepository;
import com.insurance.app.auth.entity.Users;
import com.insurance.app.auth.repository.UserRepository;
import com.insurance.app.claim.entity.Claim;
import com.insurance.app.claim.repository.ClaimRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
	@Autowired
    private NotificationRepository notificationRepository;
	@Autowired
    private UserRepository userRepository;
	@Autowired
    private ClaimRepository claimRepository;

    public ResponseEntity<Notification> sendNotification(Notification notification, Long userId, Long claimId) {
        Users user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }
        notification.setUser(user);

        if (claimId != null) {
            Claim claim = claimRepository.findById(claimId).orElse(null);
            if (claim == null) return ResponseEntity.badRequest().build();
            notification.setClaim(claim);
        } else {
            notification.setClaim(null);
        }

        return ResponseEntity.ok(notificationRepository.save(notification));
    }

    public ResponseEntity<List<Notification>> getNotificationsForUser(Long userId) {
        return ResponseEntity.ok(notificationRepository.findByUser_Id(userId));
    }

    public ResponseEntity<Void> markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElse(null);
        if (notification == null) {
            return ResponseEntity.notFound().build();
        }
        notification.setRead(true);
        notificationRepository.save(notification);
        return ResponseEntity.noContent().build();
    }
}
