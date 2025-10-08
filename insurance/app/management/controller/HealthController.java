package com.insurance.app.management.controller;

import com.insurance.app.management.DTO.HealthInsuranceDTO;
import com.insurance.app.management.service.HealthInsuranceService;
import com.insurance.app.purchase.dto.PolicyPurchaseRequest;
import com.insurance.app.purchase.entity.PolicyPurchase;
import com.insurance.app.purchase.service.PolicyPurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
@CrossOrigin(origins="*")
@RestController
@RequestMapping("/api/customer/health")
@PreAuthorize("hasRole('CUSTOMER')")
@RequiredArgsConstructor
public class HealthController {

    private final HealthInsuranceService healthService;
    private final PolicyPurchaseService policyPurchaseService;

    @PostMapping("/addHealth")
    public ResponseEntity<HealthInsuranceDTO> create(@RequestBody HealthInsuranceDTO dto) {
        HealthInsuranceDTO created = healthService.addHealth(dto);
        return ResponseEntity.status(201).body(created);
    }

    @PostMapping("/confirm/{healthId}")
    public ResponseEntity<HealthInsuranceDTO> confirm(@PathVariable Long healthId,
                                                   @RequestBody PolicyPurchaseRequest request) {
        request.setHealthPolicyId(healthId);

        if (request.getPurchaseDate() == null) {
            request.setPurchaseDate(LocalDate.now());
        }
        if (request.getExpiryDate() == null) {
            request.setExpiryDate(LocalDate.now().plusYears(1));
        }

        PolicyPurchase purchase = policyPurchaseService.createPurchase(request);
        HealthInsuranceDTO confirmed = healthService.confirmPurchase(healthId, purchase);
        return ResponseEntity.ok(confirmed);
    }

    @DeleteMapping("/cancel/{id}")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        healthService.cancelPendingHealth(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<HealthInsuranceDTO> update(@PathVariable Long id, @RequestBody HealthInsuranceDTO dto) {
    	HealthInsuranceDTO updated = healthService.updateHealth(id, dto);
        return ResponseEntity.ok(updated);
        }

    @GetMapping("/get")
    public ResponseEntity<List<HealthInsuranceDTO>> getAll() {
        return ResponseEntity.ok(healthService.getAll());
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteHealth(@PathVariable Long id) {
        healthService.deleteHealth(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/calculate-premium")
    public ResponseEntity<Double> calculatePremium(@RequestBody HealthInsuranceDTO dto) {
        return ResponseEntity.ok(healthService.computePremium(dto));
    }

    @PutMapping("/cancel/{id}")
    public ResponseEntity<Void> cancelHealthPolicy(@PathVariable Long id) {
        healthService.cancelHealthPolicy(id);
        return ResponseEntity.noContent().build();
    }
}
