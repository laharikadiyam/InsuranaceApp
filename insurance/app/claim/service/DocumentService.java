package com.insurance.app.claim.service;


import com.insurance.app.auth.entity.Users;
import com.insurance.app.auth.repository.UserRepository;
import com.insurance.app.claim.dto.DocumentDTO;
import com.insurance.app.claim.entity.Claim;
import com.insurance.app.claim.entity.Document;
import com.insurance.app.claim.repository.ClaimRepository;
import com.insurance.app.claim.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClaimRepository claimRepository;

    private final DocumentRepository documentRepository;

    @Value("${document.upload.dir:uploads}")
    private String uploadDir;

    public DocumentDTO uploadDocument(DocumentDTO dto) {
        Users user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Claim claim = claimRepository.findById(dto.getClaimId())
                .orElseThrow(() -> new RuntimeException("Claim not found"));

        Document doc = Document.builder()
                .user(user)
                .claim(claim)
                .documentType(dto.getDocumentType())
                .fileUrl(dto.getFileUrl())
                .verified(false)
                .build();

        return DocumentDTO.fromEntity(documentRepository.save(doc));
    }

    public List<DocumentDTO> getAllDocuments() {
        return documentRepository.findAll().stream()
                .map(DocumentDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<DocumentDTO> getDocumentsByClaim(Long claimId) {
        return documentRepository.findByClaim_ClaimId(claimId).stream()
                .map(DocumentDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<DocumentDTO> getDocumentsByUser(Long userId) {
        return documentRepository.findByUser_Id(userId).stream()
                .map(DocumentDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public DocumentDTO getDocumentById(Long id) {
        return DocumentDTO.fromEntity(documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found")));
    }

    public String storeFile(MultipartFile file) {
        try {
            Files.createDirectories(Paths.get(uploadDir));
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path path = Paths.get(uploadDir).resolve(fileName);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            return "/" + uploadDir + "/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("File storage failed", e);
        }
    }

    public Resource loadFile(String fileName) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found: " + fileName);
            }
        } catch (Exception e) {
            throw new RuntimeException("File loading error", e);
        }
    }

    public void deleteDocument(Long id) {
        Document doc = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        try {
            Path filePath = Paths.get(uploadDir).resolve(Paths.get(doc.getFileUrl()).getFileName());
            Files.deleteIfExists(filePath);
        } catch (IOException ignored) {}

        documentRepository.deleteById(id);
    }

    public DocumentDTO replaceDocument(Long id, MultipartFile file) {
        Document existing = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        try {
            Path oldPath = Paths.get(uploadDir).resolve(Paths.get(existing.getFileUrl()).getFileName());
            Files.deleteIfExists(oldPath);
        } catch (IOException ignored) {}

        String newUrl = storeFile(file);
        existing.setFileUrl(newUrl);
        return DocumentDTO.fromEntity(documentRepository.save(existing));
    }

    public DocumentDTO verifyDocument(Long id) {
        Document doc = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        doc.setVerified(true);
        Document updated = documentRepository.save(doc);
        return DocumentDTO.fromEntity(updated);
    }
}