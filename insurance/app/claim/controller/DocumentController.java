package com.insurance.app.claim.controller;

import com.insurance.app.claim.dto.DocumentDTO;
import com.insurance.app.claim.service.DocumentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins="*")
@RestController
@RequestMapping("/api/customer/documents")
@PreAuthorize("hasRole('CUSTOMER')")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentDTO> uploadDocument(
            @RequestParam Long userId,
            @RequestParam Long claimId,
            @RequestParam String documentType,
            @RequestParam MultipartFile file) {

        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number");
        }
        if (claimId == null || claimId <= 0) {
            throw new IllegalArgumentException("Claim ID must be a positive number");
        }
        if (documentType == null || documentType.trim().isEmpty()) {
            throw new IllegalArgumentException("Document type is required");
        }
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File must be provided");
        }

        String fileUrl = documentService.storeFile(file);

        DocumentDTO dto = DocumentDTO.builder()
                .userId(userId)
                .claimId(claimId)
                .documentType(documentType)
                .fileUrl(fileUrl)
                .build();

        return ResponseEntity.status(201).body(documentService.uploadDocument(dto));
    }

    @GetMapping
    public ResponseEntity<List<DocumentDTO>> getAllDocuments() {
        return ResponseEntity.ok(documentService.getAllDocuments());
    }

    @GetMapping("/claim/{claimId}")
    public ResponseEntity<List<DocumentDTO>> getDocumentsByClaim(@PathVariable Long claimId) {
        return ResponseEntity.ok(documentService.getDocumentsByClaim(claimId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<DocumentDTO>> getDocumentsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(documentService.getDocumentsByUser(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentDTO> getDocumentById(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getDocumentById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/verify")
    public ResponseEntity<DocumentDTO> verifyDocument(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.verifyDocument(id));
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) throws IOException {
        Resource resource = documentService.loadFile(fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @PutMapping(value = "/{id}/replace", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentDTO> replaceDocument(
            @PathVariable Long id,
            @RequestParam MultipartFile file) {
        return ResponseEntity.ok(documentService.replaceDocument(id, file));
    }
}