# Backend Changes Required

## 1. Update Health and Life Insurance Entities

### HealthInsurance Entity
Add `CANCELLED` status support:

```java
@Entity
public class HealthInsurance {
    // ... existing fields ...
    private String status; // PENDING, CONFIRMED, CANCELLED
    
    // ... existing code ...
}
```

### LifeInsurance Entity
Add `CANCELLED` status support:

```java
@Entity
public class LifeInsurance {
    // ... existing fields ...
    private String status; // PENDING, CONFIRMED, CANCELLED
    
    // ... existing code ...
}
```

## 2. Update PolicyPurchase Entity

Add status field to track policy status:

```java
@Entity
@Table(name = "policy_purchases")
public class PolicyPurchase {
    // ... existing fields ...
    private String status = "ACTIVE"; // ACTIVE, CANCELLED, EXPIRED
    
    // ... existing code ...
}
```

## 3. Update HealthInsuranceService

Add method to cancel confirmed policies:

```java
@Service
public class HealthInsuranceService {
    // ... existing methods ...
    
    public void cancelHealthPolicy(Long id) {
        HealthInsurance health = healthRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Health record not found"));
        
        if ("CONFIRMED".equals(health.getStatus())) {
            health.setStatus("CANCELLED");
            healthRepository.save(health);
            
            // Also update the purchase record
            if (health.getPurchase() != null) {
                PolicyPurchase purchase = health.getPurchase();
                purchase.setStatus("CANCELLED");
                purchaseRepository.save(purchase);
            }
        } else if ("PENDING".equals(health.getStatus())) {
            healthRepository.delete(health);
        }
    }
}
```

## 4. Update LifeInsuranceService

Add method to cancel confirmed policies:

```java
@Service
public class LifeInsuranceService {
    // ... existing methods ...
    
    public void cancelLifePolicy(Long id) {
        LifeInsurance life = lifeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Life record not found"));
        
        if ("CONFIRMED".equals(life.getStatus())) {
            life.setStatus("CANCELLED");
            lifeRepository.save(life);
            
            // Also update the purchase record
            if (life.getPurchase() != null) {
                PolicyPurchase purchase = life.getPurchase();
                purchase.setStatus("CANCELLED");
                purchaseRepository.save(purchase);
            }
        } else if ("PENDING".equals(life.getStatus())) {
            lifeRepository.delete(life);
        }
    }
}
```

## 5. Update PolicyPurchaseService

Update the cancelPurchase method to handle status updates:

```java
@Service
public class PolicyPurchaseService {
    // ... existing methods ...
    
    public void cancelPurchase(Long id) {
        PolicyPurchase purchase = getPurchaseById(id);
        
        if (purchase.getExpiryDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Cannot cancel an already expired policy");
        }
        
        // Update status instead of deleting
        purchase.setStatus("CANCELLED");
        purchaseRepository.save(purchase);
        
        // Update related policy status
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
    
    // Update getPurchasesByUser to handle status filtering properly
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
```

## 6. Update PolicyPurchaseRepository

Add new query methods:

```java
@Repository
public interface PolicyPurchaseRepository extends JpaRepository<PolicyPurchase, Long> {
    // ... existing methods ...
    
    List<PolicyPurchase> findByUser_IdAndStatusAndExpiryDateAfter(Long userId, String status, LocalDate date);
    List<PolicyPurchase> findByUser_IdAndStatus(Long userId, String status);
}
```

## 7. Update Controllers

### HealthController
Add new endpoint for cancelling confirmed policies:

```java
@RestController
@RequestMapping("/api/customer/health")
public class HealthController {
    // ... existing methods ...
    
    @PutMapping("/cancel/{id}")
    public ResponseEntity<Void> cancelHealthPolicy(@PathVariable Long id) {
        healthService.cancelHealthPolicy(id);
        return ResponseEntity.noContent().build();
    }
}
```

### LifeController
Add new endpoint for cancelling confirmed policies:

```java
@RestController
@RequestMapping("/api/customer/life")
public class LifeController {
    // ... existing methods ...
    
    @PutMapping("/cancel/{id}")
    public ResponseEntity<Void> cancelLifePolicy(@PathVariable Long id) {
        lifeService.cancelLifePolicy(id);
        return ResponseEntity.noContent().build();
    }
}
```

### PolicyPurchaseController
Update the cancel endpoint:

```java
@RestController
@RequestMapping("/api/customer/purchases")
public class PolicyPurchaseController {
    // ... existing methods ...
    
    @PutMapping("/cancel/{id}")
    public ResponseEntity<Void> cancelPurchase(@PathVariable Long id) {
        purchaseService.cancelPurchase(id);
        return ResponseEntity.noContent().build();
    }
}
```

## 8. Database Migration

Add the status column to the policy_purchases table:

```sql
ALTER TABLE policy_purchases ADD COLUMN status VARCHAR(20) DEFAULT 'ACTIVE';
ALTER TABLE health_insurance ADD COLUMN status VARCHAR(20) DEFAULT 'PENDING';
ALTER TABLE life_insurance ADD COLUMN status VARCHAR(20) DEFAULT 'PENDING';
```

## Summary of Changes

1. **Status Management**: Added proper status tracking for all policy types (PENDING, CONFIRMED, CANCELLED, EXPIRED)
2. **Cancellation Logic**: Implemented proper cancellation that updates status instead of deleting records
3. **Filtering Fix**: Fixed the inactive policies filtering to properly show cancelled and expired policies
4. **Frontend Integration**: Updated frontend to handle the new status-based cancellation system
5. **Data Integrity**: Ensured that when a purchase is cancelled, all related policy records are updated accordingly

These changes will resolve the issues with:
- Policy cancellation not working properly
- Filtering not showing inactive policies correctly
- Frontend not handling cancelled policies appropriately
- Health and life insurance modules not being accessible from the frontend
