package com.insurance.app.catalog.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.insurance.app.catalog.entity.Policy;

public interface PolicyRepository extends JpaRepository<Policy, Long>, JpaSpecificationExecutor<Policy> {
	List<Policy> findByTypeIgnoreCase(String type);
    List<Policy> findByActive(Boolean active);
    List<Policy> findByTypeIgnoreCaseAndActive(String type, Boolean active);
}
