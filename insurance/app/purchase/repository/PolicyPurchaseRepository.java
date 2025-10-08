package com.insurance.app.purchase.repository;

import com.insurance.app.purchase.entity.PolicyPurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PolicyPurchaseRepository extends JpaRepository<PolicyPurchase, Long> {

    List<PolicyPurchase> findByUser_IdAndExpiryDateAfter(Long userId, LocalDate date);

    List<PolicyPurchase> findByUser_IdAndExpiryDateBefore(Long userId, LocalDate date);
    List<PolicyPurchase> findByUser_IdAndStatusAndExpiryDateAfter(Long userId, String status, LocalDate date);
    List<PolicyPurchase> findByUser_IdAndStatus(Long userId, String status);
    List<PolicyPurchase> findByUser_Id(Long userId);

    List<PolicyPurchase> findByUser_IdAndStatusIn(Long userId, List<String> list);
}
