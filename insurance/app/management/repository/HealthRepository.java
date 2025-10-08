package com.insurance.app.management.repository;

import com.insurance.app.management.entity.HealthInsurance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HealthRepository extends JpaRepository<HealthInsurance, Long> {
}
