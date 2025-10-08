package com.insurance.app.catalog.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.insurance.app.catalog.dto.PolicyDto;

public interface PolicyService {
	PolicyDto createPolicy(PolicyDto policyDto);
    PolicyDto updatePolicy(Long id, PolicyDto policyDto);
    PolicyDto getPolicyById(Long id);
    List<PolicyDto> getAllPolicies(String type, Boolean activeOnly);
    void deletePolicy(Long id);

}
