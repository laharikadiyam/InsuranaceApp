package com.insurance.app.management.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.insurance.app.management.DTO.BikeDTO;
import com.insurance.app.management.service.BikeService;
import com.insurance.app.purchase.dto.PolicyPurchaseRequest;
import com.insurance.app.purchase.entity.PolicyPurchase;
import com.insurance.app.purchase.service.PolicyPurchaseService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
@CrossOrigin(origins="*")
@RestController
@RequestMapping("/api/customer/bike")
@PreAuthorize("hasRole('CUSTOMER')")
@RequiredArgsConstructor
public class BikeController {

    private final BikeService bikeService;
    private final PolicyPurchaseService policyPurchaseService;

    @PostMapping("/addBike")
    public ResponseEntity<BikeDTO> create(@RequestBody BikeDTO bikeObj) {
        BikeDTO created = bikeService.addVehicle(bikeObj);
        return ResponseEntity.status(201).body(created);
    }

    @PostMapping("/confirm/{bikeId}")
    public ResponseEntity<BikeDTO> confirmPurchase(@PathVariable Long bikeId,
                                                   @RequestBody PolicyPurchaseRequest request) {
        // Link purchase to this bike
        request.setBikePolicyId(bikeId);

        // Default dates if not provided
        if (request.getPurchaseDate() == null) {
            request.setPurchaseDate(LocalDate.now());
        }
        if (request.getExpiryDate() == null) {
            request.setExpiryDate(LocalDate.now().plusYears(1));
        }

        // Create purchase and confirm bike
        PolicyPurchase purchase = policyPurchaseService.createPurchase(request);
        BikeDTO confirmed = bikeService.confirmPurchase(bikeId, purchase);
        return ResponseEntity.ok(confirmed);
    }

    @DeleteMapping("/cancel/{id}")
    public ResponseEntity<Void> cancelPendingBike(@PathVariable Long id) {
        bikeService.cancelPendingBike(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/updateBike/{id}")
    public ResponseEntity<BikeDTO> update(@PathVariable Long id, @RequestBody BikeDTO bikeObj) {
        BikeDTO updated = bikeService.updatePolicy(id, bikeObj);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/get")
    public ResponseEntity<List<BikeDTO>> getBikes() {
        return ResponseEntity.ok(bikeService.getBikes());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteBike(@PathVariable Long id) {
        bikeService.deleteBike(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/calculate-premium")
    public ResponseEntity<Map<String, Double>> calculateBikePremium(@RequestBody BikeDTO bikeDTO) {
        Map<String, Double> result = bikeService.computePremiumsAndIdv(bikeDTO);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/cancel/{id}")
    public ResponseEntity<Void> cancelBike(@PathVariable Long id) {
        bikeService.cancelBike(id);
        return ResponseEntity.ok().build();
    }
}
