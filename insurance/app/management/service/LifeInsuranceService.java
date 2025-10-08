package com.insurance.app.management.service;

import com.insurance.app.auth.entity.Users;
import com.insurance.app.auth.repository.UserRepository;
import com.insurance.app.catalog.MessageUtil;
import com.insurance.app.catalog.exception.ResourceNotFoundException;
import com.insurance.app.management.DTO.LifeInsuranceDTO;
import com.insurance.app.management.entity.LifeInsurance;
import com.insurance.app.management.repository.LifeRepository;
import com.insurance.app.purchase.entity.PolicyPurchase;
import com.insurance.app.purchase.repository.PolicyPurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LifeInsuranceService {

    private final LifeRepository lifeRepository;
    private final PolicyPurchaseRepository purchaseRepository;
    private final UserRepository userRepository;
    private final MessageUtil messageUtil;

    public LifeInsuranceDTO addLife(LifeInsuranceDTO dto) {
        Users user = userRepository.findById(dto.getUser_id())
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageUtil.getMessage("user.not.found")));

        LifeInsurance life = convertToEntity(dto);
        double premium = computePremium(dto);

        life.setPremium(premium);
        life.setStatus("PENDING");
        life.setUser(user);
        life.setPurchase(null);

        return convertToDto(lifeRepository.save(life));
    }

    public LifeInsuranceDTO confirmPurchase(Long lifeId, PolicyPurchase purchase) {
        LifeInsurance l = lifeRepository.findById(lifeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageUtil.getMessage("life.not.found")));
        l.setPurchase(purchase);
        l.setStatus("CONFIRMED");
        return convertToDto(lifeRepository.save(l));
    }

    public void cancelPendingLife(Long id) {
        LifeInsurance l = lifeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageUtil.getMessage("life.not.found")));
        if ("PENDING".equals(l.getStatus())) {
            lifeRepository.delete(l);
        }
    }

    public LifeInsuranceDTO updateLife(Long id, LifeInsuranceDTO dto) {
        LifeInsurance l = lifeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageUtil.getMessage("life.not.found")));

        l.setAge(dto.getAge());
        l.setGender(dto.getGender());
        l.setSumAssured(dto.getSumAssured());
        l.setPolicyTerm(dto.getPolicyTerm());
        l.setSmoker(dto.isSmoker());
        l.setOccupationRisk(dto.getOccupationRisk());

        double premium = computePremium(dto);
        l.setPremium(premium);

        return convertToDto(lifeRepository.save(l));
    }

    public List<LifeInsuranceDTO> getAll() {
        return lifeRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private LifeInsurance convertToEntity(LifeInsuranceDTO dto) {
        return LifeInsurance.builder()
                .age(dto.getAge())
                .gender(dto.getGender())
                .sumAssured(dto.getSumAssured())
                .policyTerm(dto.getPolicyTerm())
                .smoker(dto.isSmoker())
                .occupationRisk(dto.getOccupationRisk())
                .build();
    }

    public void deleteLife(Long id) {
        if (!lifeRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    messageUtil.getMessage("life.not.found.with.id", id));
        }
        lifeRepository.deleteById(id);
    }

    public void cancelLifePolicy(Long id) {
        LifeInsurance life = lifeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageUtil.getMessage("life.not.found")));

        if ("CONFIRMED".equals(life.getStatus())) {
            life.setStatus("CANCELLED");
            lifeRepository.save(life);

            if (life.getPurchase() != null) {
                PolicyPurchase purchase = life.getPurchase();
                purchase.setStatus("CANCELLED");
                purchaseRepository.save(purchase);
            }
        } else if ("PENDING".equals(life.getStatus())) {
            lifeRepository.delete(life);
        }
    }

    private LifeInsuranceDTO convertToDto(LifeInsurance life) {
        return LifeInsuranceDTO.builder()
                .id(life.getId())
                .age(life.getAge())
                .gender(life.getGender())
                .sumAssured(life.getSumAssured())
                .policyTerm(life.getPolicyTerm())
                .smoker(life.isSmoker())
                .occupationRisk(life.getOccupationRisk())
                .premium(life.getPremium())
                .user_id(life.getUser().getId())
                .build();
    }

    public double computePremium(LifeInsuranceDTO dto) {
        int age = dto.getAge();
        if (age < 18 || age > 70) {
            throw new IllegalArgumentException(
                    messageUtil.getMessage("life.age.invalid"));
        }

        double basePremium = (dto.getSumAssured() / 1000) * 0.5;
        basePremium *= (dto.getPolicyTerm() / 10.0);

        double ageMultiplier = 1.0;
        if (age <= 25) ageMultiplier = 1.0;
        else if (age <= 35) ageMultiplier = 1.1;
        else if (age <= 45) ageMultiplier = 1.2;
        else if (age <= 55) ageMultiplier = 1.4;
        else if (age <= 65) ageMultiplier = 1.6;
        else ageMultiplier = 2.0;

        basePremium *= ageMultiplier;

        if (dto.isSmoker()) basePremium *= 1.3;

        if ("high".equalsIgnoreCase(dto.getOccupationRisk())) {
            basePremium *= 1.4;
        } else if ("medium".equalsIgnoreCase(dto.getOccupationRisk())) {
            basePremium *= 1.2;
        }

        return roundToTwoDecimal(basePremium);
    }

    private double roundToTwoDecimal(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
