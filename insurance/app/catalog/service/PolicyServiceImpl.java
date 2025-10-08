package com.insurance.app.catalog.service;

import com.insurance.app.catalog.MessageUtil;
import com.insurance.app.catalog.dto.PolicyDto;
import com.insurance.app.catalog.entity.Policy;
import com.insurance.app.catalog.exception.ResourceNotFoundException;
import com.insurance.app.catalog.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PolicyServiceImpl implements PolicyService {

	private final PolicyRepository policyRepository;
    private final MessageUtil messageUtil;

    @Override
    public PolicyDto createPolicy(PolicyDto dto) {
        Policy policy = convertToEntity(dto);
        return convertToDto(policyRepository.save(policy));
    }

    @Override
    public PolicyDto updatePolicy(Long id, PolicyDto dto) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(messageUtil.getMessage("policy.notfound", id)));

        policy.setPolicyName(dto.getPolicyName());
        policy.setType(dto.getType());
        policy.setPremium(dto.getPremium());
        policy.setTenure(dto.getTenure());
        policy.setCoverage(dto.getCoverage());
        policy.setActive(dto.isActive());

        return convertToDto(policyRepository.save(policy));
    }

    @Override
    public void deletePolicy(Long id) {
        if (!policyRepository.existsById(id)) {
            throw new ResourceNotFoundException(messageUtil.getMessage("policy.notfound", id));
        }
        policyRepository.deleteById(id);
    }

    @Override
    public PolicyDto getPolicyById(Long id) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(messageUtil.getMessage("policy.notfound", id)));
        return convertToDto(policy);
    }

    @Override
    public List<PolicyDto> getAllPolicies(String type, Boolean activeOnly) {
        List<Policy> policies;

        if (type != null && activeOnly != null) {
            policies = policyRepository.findByTypeIgnoreCaseAndActive(type, activeOnly);
        } else if (type != null) {
            policies = policyRepository.findByTypeIgnoreCase(type);
        } else if (activeOnly != null) {
            policies = policyRepository.findByActive(activeOnly);
        } else {
            policies = policyRepository.findAll();
        }

        return policies.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private Policy convertToEntity(PolicyDto dto) {
        Policy policy = new Policy();
        policy.setPolicyName(dto.getPolicyName());
        policy.setType(dto.getType());
        policy.setPremium(dto.getPremium());
        policy.setTenure(dto.getTenure());
        policy.setCoverage(dto.getCoverage());
        policy.setActive(dto.isActive());
        return policy;
    }

    private PolicyDto convertToDto(Policy policy) {
        PolicyDto dto = new PolicyDto();
        dto.setPolicy_id(policy.getPolicy_id());
        dto.setPolicyName(policy.getPolicyName());
        dto.setType(policy.getType());
        dto.setPremium(policy.getPremium());
        dto.setTenure(policy.getTenure());
        dto.setCoverage(policy.getCoverage());
        dto.setActive(policy.isActive());
        return dto;
    }

}
