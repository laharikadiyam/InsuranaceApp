# Backend Changes for Policy Cancellation and Unified Policy View

## Issue Description
Currently, when bike and car policies are cancelled, they show as "active" instead of "cancelled" in the frontend. This is because the backend cancellation logic needs to be updated to properly handle status changes. Additionally, the claim module needs to be updated to only allow claims for active policies.

## Required Backend Changes

### 1. Update Bike Entity
Add `CANCELLED` status support to the `Bike` entity:

```java
@Entity
public class Bike {
    // ... existing fields ...
    
    private String status; // PENDING, CONFIRMED, CANCELLED
    
    // ... rest of the entity
}
```

### 2. Update Car Entity
Add `CANCELLED` status support to the `Car` entity:

```java
@Entity
public class Car {
    // ... existing fields ...
    
    private String status; // PENDING, CONFIRMED, CANCELLED
    
    // ... rest of the entity
}
```

### 3. Update BikeService
Modify the `cancelPendingBike` method to update status instead of deleting:

```java
@Service
public class BikeService {
    // ... existing methods ...
    
    public void cancelPendingBike(Long id) {
        Bike bike = bikeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bike not found"));
        
        // Update status to CANCELLED instead of deleting
        bike.setStatus("CANCELLED");
        bikeRepository.save(bike);
        
        // Also update the associated PolicyPurchase if it exists
        if (bike.getPurchase() != null) {
            PolicyPurchase purchase = bike.getPurchase();
            purchase.setStatus("CANCELLED");
            policyPurchaseRepository.save(purchase);
        }
    }
    
    // Add method to cancel confirmed bike
    public void cancelBike(Long id) {
        Bike bike = bikeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bike not found"));
        
        bike.setStatus("CANCELLED");
        bikeRepository.save(bike);
        
        // Also update the associated PolicyPurchase if it exists
        if (bike.getPurchase() != null) {
            PolicyPurchase purchase = bike.getPurchase();
            purchase.setStatus("CANCELLED");
            policyPurchaseRepository.save(purchase);
        }
    }
}
```

### 4. Update CarService
Modify the `cancelPendingCar` method to update status instead of deleting:

```java
@Service
public class CarService {
    // ... existing methods ...
    
    public void cancelPendingCar(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found"));
        
        // Update status to CANCELLED instead of deleting
        car.setStatus("CANCELLED");
        carRepository.save(car);
        
        // Also update the associated PolicyPurchase if it exists
        if (car.getPurchase() != null) {
            PolicyPurchase purchase = car.getPurchase();
            purchase.setStatus("CANCELLED");
            policyPurchaseRepository.save(purchase);
        }
    }
    
    // Add method to cancel confirmed car
    public void cancelCar(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found"));
        
        car.setStatus("CANCELLED");
        carRepository.save(car);
        
        // Also update the associated PolicyPurchase if it exists
        if (car.getPurchase() != null) {
            PolicyPurchase purchase = car.getPurchase();
            purchase.setStatus("CANCELLED");
            policyPurchaseRepository.save(purchase);
        }
    }
}
```

### 5. Update BikeController
Add new endpoint for cancelling confirmed bikes:

```java
@RestController
@RequestMapping("/api/customer/bike")
public class BikeController {
    // ... existing methods ...
    
    @PutMapping("/cancel/{id}")
    public ResponseEntity<Void> cancelBike(@PathVariable Long id) {
        bikeService.cancelBike(id);
        return ResponseEntity.ok().build();
    }
    
    // Update existing cancel endpoint to handle both pending and confirmed
    @DeleteMapping("/cancel/{id}")
    public ResponseEntity<Void> cancelPendingBike(@PathVariable Long id) {
        bikeService.cancelPendingBike(id);
        return ResponseEntity.noContent().build();
    }
}
```

### 6. Update CarController
Add new endpoint for cancelling confirmed cars:

```java
@RestController
@RequestMapping("/api/customer/car")
public class CarController {
    // ... existing methods ...
    
    @PutMapping("/cancel/{id}")
    public ResponseEntity<Void> cancelCar(@PathVariable Long id) {
        carService.cancelCar(id);
        return ResponseEntity.ok().build();
    }
    
    // Update existing cancel endpoint to handle both pending and confirmed
    @DeleteMapping("/cancel/{id}")
    public ResponseEntity<Void> cancelPendingCar(@PathVariable Long id) {
        carService.cancelPendingCar(id);
        return ResponseEntity.noContent().build();
    }
}
```

### 7. Update PolicyPurchaseService
Ensure the `cancelPurchase` method properly updates bike and car statuses:

```java
@Service
public class PolicyPurchaseService {
    // ... existing methods ...
    
    public void cancelPurchase(Long purchaseId) {
        PolicyPurchase purchase = policyPurchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase not found"));
        
        purchase.setStatus("CANCELLED");
        policyPurchaseRepository.save(purchase);
        
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
            healthInsuranceRepository.save(health);
        }
        
        if (purchase.getLifePolicy() != null) {
            LifeInsurance life = purchase.getLifePolicy();
            life.setStatus("CANCELLED");
            lifeInsuranceRepository.save(life);
        }
    }
}
```

### 8. Update ClaimService (MODIFY EXISTING)
Add policy status validation to your existing `raiseClaim` method:

```java
@Service
@RequiredArgsConstructor
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final UserRepository userRepository;
    private final PolicyPurchaseRepository purchaseRepository;

    /**
     * Create a new claim - MODIFIED TO ADD POLICY STATUS VALIDATION
     */
    public Claim raiseClaim(ClaimDTO claimDTO) {
        // Validate user exists
        Users user = userRepository.findById(claimDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + claimDTO.getUserId()));

        // Validate purchase exists
        PolicyPurchase purchase = purchaseRepository.findById(claimDTO.getPurchaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Purchase not found with ID: " + claimDTO.getPurchaseId()));

        // Validate that the purchase belongs to the user
        if (!purchase.getUser().getId().equals(claimDTO.getUserId())) {
            throw new RuntimeException("Purchase does not belong to the specified user");
        }

        // NEW: Validate that the policy is active (CONFIRMED or ACTIVE status)
        if (!"CONFIRMED".equals(purchase.getStatus()) && !"ACTIVE".equals(purchase.getStatus())) {
            throw new RuntimeException("Claims can only be submitted for active policies. This policy has status: " + purchase.getStatus());
        }

        // Check if a claim already exists for this purchase
        if (claimRepository.existsByPurchase_PurchaseId(claimDTO.getPurchaseId())) {
            throw new RuntimeException("A claim already exists for this purchase");
        }

        // Create new claim
        Claim claim = Claim.builder()
                .claimStatus("PENDING") // Always start with PENDING status
                .user(user)
                .purchase(purchase)
                .uploadedAt(LocalDateTime.now())
                .build();

        return claimRepository.save(claim);
    }

    // ... rest of your existing methods remain the same ...

    /**
     * NEW: Get only active purchases for claims
     */
    public List<PolicyPurchase> getActivePurchasesForClaims(Long userId) {
        // Validate user exists
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }
        
        return purchaseRepository.findByUser_IdAndStatusIn(
            userId, 
            Arrays.asList("CONFIRMED", "ACTIVE")
        );
    }
}
```

### 9. Update ClaimController (ADD NEW ENDPOINT)
Add a new endpoint to your existing `ClaimController`:

```java
@CrossOrigin(origins="*")
@RestController
@RequestMapping("/api/customer/claims")
@PreAuthorize("hasRole('CUSTOMER')")
@RequiredArgsConstructor
public class ClaimController {
    private final ClaimService claimService;

    // ... your existing methods remain the same ...

    /**
     * NEW: Get only active policies for claims
     */
    @GetMapping("/active-policies/{userId}")
    public ResponseEntity<?> getActivePoliciesForClaims(@PathVariable Long userId) {
        try {
            List<PolicyPurchase> activePolicies = claimService.getActivePurchasesForClaims(userId);
            return ResponseEntity.ok(activePolicies);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to get active policies: " + e.getMessage());
        }
    }
}
```

### 10. Update PolicyPurchaseRepository (ADD NEW METHOD)
Add this method to your existing `PolicyPurchaseRepository`:

```java
@Repository
public interface PolicyPurchaseRepository extends JpaRepository<PolicyPurchase, Long> {
    // ... your existing methods ...
    
    /**
     * NEW: Find purchases by user ID and status list
     */
    List<PolicyPurchase> findByUser_IdAndStatusIn(Long userId, List<String> statuses);
}
```

**IMPORTANT**: Do NOT add this method to `ClaimRepository`. The `Claim` entity doesn't have a `status` property, which is causing the error.

### 11. Database Migration
Add the `CANCELLED` status to existing status columns if needed:

```sql
-- Update existing bike and car records to have proper status
UPDATE bike SET status = 'PENDING' WHERE status IS NULL;
UPDATE car SET status = 'PENDING' WHERE status IS NULL;

-- Ensure the status column can accept 'CANCELLED' value
-- (This should already be the case if using VARCHAR)
```

### 12. Update DTOs
Ensure the DTOs include the status field:

```java
@Data
public class BikeDTO {
    // ... existing fields ...
    private String status;
}

@Data
public class CarDTO {
    // ... existing fields ...
    private String status;
}
```

### 13. Update Repository Methods
Add methods to filter by status:

```java
@Repository
public interface BikeRepository extends JpaRepository<Bike, Long> {
    List<Bike> findByStatus(String status);
    List<Bike> findByUser_IdAndStatus(Long userId, String status);
}

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    List<Car> findByStatus(String status);
    List<Car> findByUser_IdAndStatus(Long userId, String status);
}
```

## Summary of Changes
1. **Status Management**: Update bike and car entities to properly handle `CANCELLED` status
2. **Cancellation Logic**: Modify cancellation methods to update status instead of deleting records
3. **API Endpoints**: Add new endpoints for cancelling confirmed policies
4. **Data Consistency**: Ensure PolicyPurchase and associated policy statuses are updated together
5. **Repository Methods**: Add methods to filter policies by status
6. **Claim Restrictions**: Modified existing ClaimService to validate policy status before allowing claims
7. **New Endpoint**: Added endpoint to get only active policies for claims

## Testing
After implementing these changes:
1. Test cancelling pending bike/car policies
2. Test cancelling confirmed bike/car policies
3. Verify that cancelled policies show as "CANCELLED" in the frontend
4. Verify that cancelled policies cannot be renewed or edited
5. Test the unified policy view with all policy types
6. Test that only active policies can be used for claims
7. Verify that cancelled policies are not available in claim dropdown
8. Test the new `/api/customer/claims/active-policies/{userId}` endpoint

## Frontend Integration
The frontend changes have already been made to:
1. Remove separate health and life insurance buttons
2. Update policy-view component to show admin-created policies
3. Handle status-based filtering and display
4. Disable actions for cancelled policies
5. Restrict claim creation to only active policies

## Flow Description

### Customer Policy View Flow:
1. **Policy View**: Customer sees admin-created policies (PolicyDto objects)
2. **Policy Selection**: Customer clicks on a policy to view details
3. **Form Display**: Vehicle-management component shows form based on policy type
4. **Details Entry**: Customer fills in their specific details (vehicle info, personal info, etc.)
5. **Premium Calculation**: Customer clicks "Calculate Premium" button
   - Backend calculates premium based on customer's details
   - Creates entity in respective table with PENDING status
6. **Confirmation/Cancellation**: 
   - **Confirm Purchase**: Changes status to CONFIRMED and creates PolicyPurchase entry
   - **Cancel Purchase**: Deletes the entity from respective table

### Claim Flow:
1. **Claim Access**: Only active (CONFIRMED/ACTIVE) policies are available for claims
2. **Policy Selection**: User can only select from active policies in dropdown
3. **Claim Submission**: Backend validates policy status before allowing claim
4. **Status Display**: Claims table shows both policy status and claim status

### Key Points:
- Policy-view shows admin-created policies (not user's own policies)
- Each user creates their own instance of the policy with their specific details
- Premium calculation is based on user's details, not the admin policy
- Cancellation before confirmation deletes the entity
- Cancellation after confirmation updates status to CANCELLED
- Only active policies can be used for claims
- Cancelled policies are not eligible for claims
