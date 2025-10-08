package com.insurance.app.claim.repository;


import com.insurance.app.claim.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByClaim_ClaimId(Long claimId);
    List<Document> findByUser_Id(Long userId);
}

