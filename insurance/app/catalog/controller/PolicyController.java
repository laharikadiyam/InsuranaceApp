package com.insurance.app.catalog.controller;

import com.insurance.app.catalog.dto.PolicyDto;
import com.insurance.app.catalog.service.PolicyService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/admin/policies")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class PolicyController
{
	private final PolicyService policyService;

    @PostMapping
    public ResponseEntity<PolicyDto> create(@Valid @RequestBody PolicyDto policyDto) {
        PolicyDto created = policyService.createPolicy(policyDto);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PolicyDto> update(@Valid @PathVariable Long id, @RequestBody PolicyDto policyDto) {
        PolicyDto updated = policyService.updatePolicy(id, policyDto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PolicyDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(policyService.getPolicyById(id));
    }

    @GetMapping
    public ResponseEntity<List<PolicyDto>> getAll(@RequestParam(required = false) String type,
                                                  @RequestParam(required = false) Boolean activeOnly) {
        return ResponseEntity.ok(policyService.getAllPolicies(type, activeOnly));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePolicy(@PathVariable Long id) {
        policyService.deletePolicy(id);
        return ResponseEntity.noContent().build();
    }

}
