package com.insurance.app.claim.controller;

import com.insurance.app.claim.dto.NotificationDTO;
import com.insurance.app.claim.entity.Notification;
import com.insurance.app.claim.service.NotificationService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins="*")
@RestController
@RequestMapping("/api/customer/notifications")
@PreAuthorize("hasRole('CUSTOMER')")
@RequiredArgsConstructor
public class NotificationController {
	@Autowired
    private NotificationService notificationService;

    private Notification toEntity(NotificationDTO dto) {
        return Notification.builder()
                .notificationId(dto.getNotificationId())
                .message(dto.getMessage())
                .isRead(dto.isRead())
                .createdAt(dto.getCreatedAt())
                .build();
    }

    @PostMapping("/sendNotification")
    public ResponseEntity<Notification> sendNotification(@RequestBody NotificationDTO dto) {
        Notification notification = toEntity(dto);
        return notificationService.sendNotification(notification, dto.getUserId(), dto.getClaimId());
    }

    @GetMapping("/getNotificationsByUser/{userId}")
    public ResponseEntity<List<Notification>> getNotificationsByUser(@PathVariable Long userId) {
        return notificationService.getNotificationsForUser(userId);
    }

    @PutMapping("/markAsRead/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        return notificationService.markAsRead(id);
    }
}