package com.insurance.app.management.controller;

import com.insurance.app.management.DTO.CarDTO;
import com.insurance.app.management.service.CarService;
import com.insurance.app.purchase.dto.PolicyPurchaseRequest;
import com.insurance.app.purchase.service.PolicyPurchaseService;
import com.insurance.app.purchase.entity.PolicyPurchase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
@CrossOrigin(origins="*")
@RestController
@RequestMapping("/api/customer/car")
@PreAuthorize("hasRole('CUSTOMER')")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;
    private final PolicyPurchaseService policyPurchaseService;

    @PostMapping("/addCar")
    public ResponseEntity<CarDTO> create(@RequestBody CarDTO carObj) {
        CarDTO created = carService.addCar(carObj);
        return ResponseEntity.status(201).body(created);
    }

    @PostMapping("/confirm/{carId}")
    public ResponseEntity<CarDTO> confirmPurchase(@PathVariable Long carId,
                                                  @RequestBody PolicyPurchaseRequest request) {
        request.setCarPolicyId(carId);

        if (request.getPurchaseDate() == null) {
            request.setPurchaseDate(LocalDate.now());
        }
        if (request.getExpiryDate() == null) {
            request.setExpiryDate(LocalDate.now().plusYears(1));
        }

        PolicyPurchase purchase = policyPurchaseService.createPurchase(request);
        CarDTO confirmed = carService.confirmPurchase(carId, purchase);
        return ResponseEntity.ok(confirmed);
    }

    @DeleteMapping("/cancel/{id}")
    public ResponseEntity<Void> cancelPendingCar(@PathVariable Long id) {
        carService.cancelPendingCar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/updateCar/{id}")
    public ResponseEntity<CarDTO> update(@PathVariable Long id, @RequestBody CarDTO carObj) {
        CarDTO updated = carService.updatePolicy(id, carObj);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/get")
    public ResponseEntity<List<CarDTO>> getCars() {
        return ResponseEntity.ok(carService.getCars());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/calculate-premium")
    public ResponseEntity<Map<String, Double>> calculateCarPremium(@RequestBody CarDTO carDTO) {
        Map<String, Double> result = carService.computePremiumsAndIdv(carDTO);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/cancel/{id}")
    public ResponseEntity<Void> cancelCar(@PathVariable Long id) {
        carService.cancelCar(id);
        return ResponseEntity.ok().build();
    }
}
