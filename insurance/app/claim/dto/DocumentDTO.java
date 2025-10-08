package com.insurance.app.claim.dto;

import com.insurance.app.claim.entity.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentDTO {
    private Long documentId;
    private Long userId;
    private Long claimId;
    private String documentType;
    private String fileUrl;
    private LocalDateTime uploadedAt;
    private boolean verified;

    public static DocumentDTO fromEntity(Document document) {
        return DocumentDTO.builder()
                .documentId(document.getDocumentId())
                .userId(document.getUser().getId())
                .claimId(document.getClaim().getClaimId())
                .documentType(document.getDocumentType())
                .fileUrl(document.getFileUrl())
                .uploadedAt(document.getUploadedAt())
                .verified(document.isVerified())
                .build();
    }
}