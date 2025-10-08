package com.insurance.app.purchase.controller;

import com.insurance.app.purchase.dto.PolicyPurchaseRequest;
import com.insurance.app.purchase.entity.PolicyPurchase;
import com.insurance.app.purchase.service.PolicyPurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins="*")
@RestController
@RequestMapping("/api/customer/purchases")
@PreAuthorize("hasRole('CUSTOMER')")
@RequiredArgsConstructor
public class PolicyPurchaseController {

    private final PolicyPurchaseService purchaseService;

    
    @PostMapping
    public ResponseEntity<PolicyPurchase> createPurchase(@RequestBody PolicyPurchaseRequest request) {
        return ResponseEntity.ok(purchaseService.createPurchase(request));
    }

   
    @GetMapping
    public ResponseEntity<List<PolicyPurchase>> getPurchasesByUser(
            @RequestParam Long userId,
            @RequestParam(required = false) Boolean activeOnly) {
        return ResponseEntity.ok(purchaseService.getPurchasesByUser(userId, activeOnly));
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<PolicyPurchase> getPurchaseById(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseService.getPurchaseById(id));
    }

    
    @PutMapping("/{id}")
    public ResponseEntity<PolicyPurchase> updatePurchase(
            @PathVariable Long id,
            @RequestBody PolicyPurchaseRequest request) {
        return ResponseEntity.ok(purchaseService.updatePurchase(id, request));
    }


    @PutMapping("/cancel/{id}")
    public ResponseEntity<Void> cancelPurchase(@PathVariable Long id) {
        purchaseService.cancelPurchase(id);
        return ResponseEntity.noContent().build();
    }
}
