package com.insurance.app.management.repository;

import com.insurance.app.management.entity.LifeInsurance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LifeRepository extends JpaRepository<LifeInsurance, Long> {
}
