package com.insurance.app.purchase.service;

import com.insurance.app.auth.repository.UserRepository;
import com.insurance.app.catalog.MessageUtil;
import com.insurance.app.catalog.exception.ResourceNotFoundException;
import com.insurance.app.management.entity.Bike;
import com.insurance.app.management.entity.Car;
import com.insurance.app.management.entity.HealthInsurance;
import com.insurance.app.management.entity.LifeInsurance;
import com.insurance.app.management.repository.BikeRepository;
import com.insurance.app.management.repository.CarRepository;
import com.insurance.app.management.repository.HealthRepository;
import com.insurance.app.management.repository.LifeRepository;
import com.insurance.app.purchase.dto.PolicyPurchaseRequest;
import com.insurance.app.purchase.entity.PolicyPurchase;
import com.insurance.app.purchase.repository.PolicyPurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PolicyPurchaseService {

    private final PolicyPurchaseRepository purchaseRepository;
    private final UserRepository usersRepository;
    private final BikeRepository bikeRepository;
    private final CarRepository carRepository;
    private final HealthRepository healthRepository;
    private final LifeRepository lifeRepository;
    private final MessageUtil messageUtil;

    public PolicyPurchase createPurchase(PolicyPurchaseRequest request) {
        PolicyPurchase purchase = new PolicyPurchase();

        purchase.setUser(usersRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageUtil.getMessage("user.notfound", request.getUserId())
                )));

        if (request.getBikePolicyId() != null) {
            purchase.setBikePolicy(bikeRepository.findById(request.getBikePolicyId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            messageUtil.getMessage("bike.policy.notfound", request.getBikePolicyId())
                    )));
        }
        if (request.getCarPolicyId() != null) {
            purchase.setCarPolicy(carRepository.findById(request.getCarPolicyId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            messageUtil.getMessage("car.policy.notfound", request.getCarPolicyId())
                    )));
        }
        if (request.getHealthPolicyId() != null) {
            purchase.setHealthPolicy(healthRepository.findById(request.getHealthPolicyId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            messageUtil.getMessage("health.policy.notfound", request.getHealthPolicyId())
                    )));
        }
        if (request.getLifePolicyId() != null) {
            purchase.setLifePolicy(lifeRepository.findById(request.getLifePolicyId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            messageUtil.getMessage("life.policy.notfound", request.getLifePolicyId())
                    )));
        }

        purchase.setPurchaseDate(request.getPurchaseDate());
        purchase.setExpiryDate(request.getExpiryDate());

        return purchaseRepository.save(purchase);
    }

    public PolicyPurchase getPurchaseById(Long id) {
        return purchaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageUtil.getMessage("purchase.notfound", id)
                ));
    }

    public PolicyPurchase updatePurchase(Long id, PolicyPurchaseRequest request) {
        PolicyPurchase existing = getPurchaseById(id);

        if (request.getBikePolicyId() != null) {
            existing.setBikePolicy(bikeRepository.findById(request.getBikePolicyId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            messageUtil.getMessage("bike.policy.notfound", request.getBikePolicyId())
                    )));
        }
        if (request.getCarPolicyId() != null) {
            existing.setCarPolicy(carRepository.findById(request.getCarPolicyId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            messageUtil.getMessage("car.policy.notfound", request.getCarPolicyId())
                    )));
        }
        if (request.getHealthPolicyId() != null) {
            existing.setHealthPolicy(healthRepository.findById(request.getHealthPolicyId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            messageUtil.getMessage("health.policy.notfound", request.getHealthPolicyId())
                    )));
        }
        if (request.getLifePolicyId() != null) {
            existing.setLifePolicy(lifeRepository.findById(request.getLifePolicyId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            messageUtil.getMessage("life.policy.notfound", request.getLifePolicyId())
                    )));
        }

        existing.setPurchaseDate(request.getPurchaseDate());
        existing.setExpiryDate(request.getExpiryDate());

        return purchaseRepository.save(existing);
    }

    public void cancelPurchase(Long purchaseId) {
        PolicyPurchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageUtil.getMessage("purchase.notfound", purchaseId)
                ));

        purchase.setStatus("CANCELLED");
        purchaseRepository.save(purchase);

        // Update the associated policy status
        if (purchase.getBikePolicy() != null) {
            Bike bike = purchase.getBikePolicy();
            bike.setStatus("CANCELLED");
            bikeRepository.save(bike);
        }

        if (purchase.getCarPolicy() != null) {
            Car car = purchase.getCarPolicy();
            car.setStatus("CANCELLED");
            carRepository.save(car);
        }

        if (purchase.getHealthPolicy() != null) {
            HealthInsurance health = purchase.getHealthPolicy();
            health.setStatus("CANCELLED");
            healthRepository.save(health);
        }

        if (purchase.getLifePolicy() != null) {
            LifeInsurance life = purchase.getLifePolicy();
            life.setStatus("CANCELLED");
            lifeRepository.save(life);
        }
    }

    public List<PolicyPurchase> getPurchasesByUser(Long userId, Boolean activeOnly) {
        if (activeOnly != null) {
            if (activeOnly) {
                // Return only active policies (not cancelled and not expired)
                return purchaseRepository.findByUser_IdAndStatusAndExpiryDateAfter(userId, "ACTIVE", LocalDate.now());
            } else {
                // Return inactive policies (cancelled or expired)
                List<PolicyPurchase> cancelled = purchaseRepository.findByUser_IdAndStatus(userId, "CANCELLED");
                List<PolicyPurchase> expired = purchaseRepository.findByUser_IdAndExpiryDateBefore(userId, LocalDate.now());
                cancelled.addAll(expired);
                return cancelled;
            }
        }
        return purchaseRepository.findByUser_Id(userId);
    }
}
