package com.insurance.app.management.repository;

import com.insurance.app.management.entity.PremiumRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PremiumRecordRepository extends JpaRepository<PremiumRecord, Long> {
}
