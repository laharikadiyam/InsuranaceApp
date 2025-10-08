package com.insurance.app.management.controller;

import com.insurance.app.management.DTO.LifeInsuranceDTO;
import com.insurance.app.management.service.LifeInsuranceService;
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
@RequestMapping("/api/customer/life")
@PreAuthorize("hasRole('CUSTOMER')")
@RequiredArgsConstructor
public class LifeController {

    private final LifeInsuranceService lifeService;
    private final PolicyPurchaseService policyPurchaseService;

    @PostMapping("/addLife")
    public ResponseEntity<LifeInsuranceDTO> create(@RequestBody LifeInsuranceDTO dto) {
    	LifeInsuranceDTO created = lifeService.addLife(dto);
        return ResponseEntity.status(201).body(created);
    }

    @PostMapping("/confirm/{lifeId}")
    public ResponseEntity<LifeInsuranceDTO> confirm(@PathVariable Long lifeId,
                                                 @RequestBody PolicyPurchaseRequest request) {
        request.setLifePolicyId(lifeId);

        if (request.getPurchaseDate() == null) {
            request.setPurchaseDate(LocalDate.now());
        }
        if (request.getExpiryDate() == null) {
            request.setExpiryDate(LocalDate.now().plusYears(1));
        }

        PolicyPurchase purchase = policyPurchaseService.createPurchase(request);
        LifeInsuranceDTO confirmed = lifeService.confirmPurchase(lifeId, purchase);
        return ResponseEntity.ok(confirmed);
    }

    @DeleteMapping("/cancel/{id}")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        lifeService.cancelPendingLife(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/updateLife/{id}")
    public ResponseEntity<LifeInsuranceDTO> update(@PathVariable Long id, @RequestBody LifeInsuranceDTO dto) {
    	LifeInsuranceDTO updated = lifeService.updateLife(id, dto);
        return ResponseEntity.ok(updated);    }

    @GetMapping("/get")
    public ResponseEntity<List<LifeInsuranceDTO>> getAll() {
        return ResponseEntity.ok(lifeService.getAll());
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteLife(@PathVariable Long id) {
        lifeService.deleteLife(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/calculate-premium")
    public ResponseEntity<Double> calculatePremium(@RequestBody LifeInsuranceDTO dto) {
        return ResponseEntity.ok(lifeService.computePremium(dto));
    }

    @PutMapping("/cancel/{id}")
    public ResponseEntity<Void> cancelLifePolicy(@PathVariable Long id) {
        lifeService.cancelLifePolicy(id);
        return ResponseEntity.noContent().build();
    }
}
