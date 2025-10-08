package com.insurance.app.management.service;

import com.insurance.app.auth.entity.Users;
import com.insurance.app.auth.repository.UserRepository;
import com.insurance.app.catalog.MessageUtil;
import com.insurance.app.catalog.exception.ResourceNotFoundException;
import com.insurance.app.management.DTO.HealthInsuranceDTO;
import com.insurance.app.management.entity.HealthInsurance;
import com.insurance.app.management.repository.HealthRepository;
import com.insurance.app.purchase.entity.PolicyPurchase;
import com.insurance.app.purchase.repository.PolicyPurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HealthInsuranceService {

    private final HealthRepository healthRepository;
    private final PolicyPurchaseRepository purchaseRepository;
    private final UserRepository userRepository;
    private final MessageUtil messageUtil;

    public HealthInsuranceDTO addHealth(HealthInsuranceDTO dto) {
        Users user = userRepository.findById(dto.getUser_id())
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageUtil.getMessage("user.not.found")));

        HealthInsurance health = convertToEntity(dto);
        double premium = computePremium(dto);
        health.setPremium(premium);
        health.setStatus("PENDING");
        health.setUser(user);
        health.setPurchase(null);

        return convertToDto(healthRepository.save(health));
    }

    public HealthInsuranceDTO confirmPurchase(Long healthId, PolicyPurchase purchase) {
        HealthInsurance h = healthRepository.findById(healthId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageUtil.getMessage("health.not.found")));
        h.setPurchase(purchase);
        h.setStatus("CONFIRMED");
        return convertToDto(healthRepository.save(h));
    }

    public void cancelPendingHealth(Long id) {
        HealthInsurance h = healthRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageUtil.getMessage("health.not.found")));
        if ("PENDING".equals(h.getStatus())) {
            healthRepository.delete(h);
        }
    }

    public void cancelHealthPolicy(Long id) {
        HealthInsurance health = healthRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageUtil.getMessage("health.not.found")));

        if ("CONFIRMED".equals(health.getStatus())) {
            health.setStatus("CANCELLED");
            healthRepository.save(health);

            if (health.getPurchase() != null) {
                PolicyPurchase purchase = health.getPurchase();
                purchase.setStatus("CANCELLED");
                purchaseRepository.save(purchase);
            }
        } else if ("PENDING".equals(health.getStatus())) {
            healthRepository.delete(health);
        }
    }

    public HealthInsuranceDTO updateHealth(Long id, HealthInsuranceDTO dto) {
        HealthInsurance h = healthRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageUtil.getMessage("health.not.found")));

        h.setAge(dto.getAge());
        h.setNumberOfMembers(dto.getNumberOfMembers());
        h.setSumInsured(dto.getSumInsured());
        h.setSmoker(dto.isSmoker());
        h.setPreExisting(dto.isPreExisting());

        double premium = computePremium(dto);
        h.setPremium(premium);

        return convertToDto(healthRepository.save(h));
    }

    public void deleteHealth(Long id) {
        if (!healthRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    messageUtil.getMessage("health.not.found.with.id", id));
        }
        healthRepository.deleteById(id);
    }

    public List<HealthInsuranceDTO> getAll() {
        return healthRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private HealthInsurance convertToEntity(HealthInsuranceDTO dto) {
        return HealthInsurance.builder()
                .age(dto.getAge())
                .numberOfMembers(dto.getNumberOfMembers())
                .sumInsured(dto.getSumInsured())
                .smoker(dto.isSmoker())
                .preExisting(dto.isPreExisting())
                .build();
    }

    private HealthInsuranceDTO convertToDto(HealthInsurance health) {
        return HealthInsuranceDTO.builder()
                .id(health.getId())
                .age(health.getAge())
                .numberOfMembers(health.getNumberOfMembers())
                .sumInsured(health.getSumInsured())
                .smoker(health.isSmoker())
                .preExisting(health.isPreExisting())
                .premium(health.getPremium())
                .user_id(health.getUser().getId())
                .build();
    }

    public double computePremium(HealthInsuranceDTO dto) {
        double basePremium = dto.getSumInsured() * 0.02;
        basePremium += dto.getNumberOfMembers() * 1000;
        if (dto.isSmoker()) basePremium += 2000;
        if (dto.isPreExisting()) basePremium += 3000;
        if (dto.getAge() > 50) basePremium *= 1.2;

        return roundToTwoDecimal(basePremium);
    }

    private double roundToTwoDecimal(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
