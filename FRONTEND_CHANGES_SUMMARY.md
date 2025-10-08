# Frontend Changes Summary - Unified Policy View

## Overview
This document summarizes all the frontend changes made to implement a unified policy view where customers can view admin-created policies and create their own instances with specific details.

## Changes Made

### 1. Customer Dashboard (`customer-dashboard.component.html`)
**File**: `src/app/components/customer-dashboard/customer-dashboard.component.html`

**Changes**:
- Removed separate "Health Insurance" and "Life Insurance" buttons
- Reorganized quick actions to have only 4 buttons: View Policies, Claims, My Purchases, Change Password
- Changed layout from `col-4` to `col-6` for better spacing

**Before**:
```html
<div class="col-4 mb-3">
  <button routerLink="/customer/dashboard/health-insurance">Health Insurance</button>
</div>
<div class="col-4 mb-3">
  <button routerLink="/customer/dashboard/life-insurance">Life Insurance</button>
</div>
```

**After**:
```html
<div class="col-6 mb-3">
  <button routerLink="/customer/dashboard/policy-view">View Policies</button>
</div>
<div class="col-6 mb-3">
  <button routerLink="/customer/dashboard/claim">Claims</button>
</div>
```

### 2. App Routes (`app.routes.ts`)
**File**: `src/app/app.routes.ts`

**Changes**:
- Removed health-insurance and life-insurance routes
- Fixed import statement for guards
- All policies now accessible through the policy-view route

**Removed Routes**:
```typescript
{ 
  path: 'customer/dashboard/health-insurance', 
  loadComponent: () => import('./components/customer-dashboard/health-insurance/health-insurance.component').then(m => m.HealthInsuranceComponent),
  canActivate: [CustomerGuard]
},
{ 
  path: 'customer/dashboard/life-insurance', 
  loadComponent: () => import('./components/customer-dashboard/life-insurance/life-insurance.component').then(m => m.LifeInsuranceComponent),
  canActivate: [CustomerGuard]
}
```

### 3. Policy View Component (`policy-view.component.ts`)
**File**: `src/app/components/customer-dashboard/policy-view/policy-view.component.ts`

**Major Changes**:
- **Admin Policy Loading**: Now loads admin-created policies (PolicyDto objects) that are available to customers
- **Enhanced Filtering**: Added proper filtering by policy type and status
- **Service Integration**: Uses PolicyService to get available policies
- **Navigation**: Routes to vehicle-management component when user clicks on a policy

**Updated Methods**:
```typescript
loadPolicies(): void // Loads admin-created policies
applyFilters(): void // Applies type and status filters
viewDetails(policy: PolicyDto): void // Navigates to vehicle-management
```

**Data Structure**:
The component now displays PolicyDto objects with:
- `policyName`: Name of the policy
- `type`: Policy type (bike, car, health, life)
- `policy_id`: Unique identifier
- `premium`: Base premium amount
- `tenure`: Policy duration in months
- `coverage`: Coverage description
- `active`: Boolean indicating if policy is active

### 4. Vehicle Management Component (`vehicle-management.component.ts`)
**File**: `src/app/components/customer-dashboard/vehicle-management/vehicle-management.component.ts`

**Major Changes**:
- **Service Integration**: Added HealthInsuranceService and LifeInsuranceService
- **Premium Calculation**: Updated to properly calculate premiums for health and life insurance
- **Flow Management**: Handles the complete flow from form filling to purchase confirmation

**Updated Methods**:
```typescript
calculateHealthAndSave(): void // Calculates premium and saves health insurance
calculateLifeAndSave(): void // Calculates premium and saves life insurance
cancelPending(): void // Cancels pending policies (deletes entity)
confirmPurchase(): void // Confirms purchase and creates PolicyPurchase entry
```

**Flow**:
1. User fills in form with their specific details
2. User clicks "Calculate Premium" - calculates premium and creates entity with PENDING status
3. User can either:
   - **Confirm Purchase**: Changes status to CONFIRMED and creates PolicyPurchase entry
   - **Cancel Purchase**: Deletes the entity from the respective table

### 5. Vehicle Service (`vehicle.service.ts`)
**File**: `src/app/services/vehicle.service.ts`

**Changes**:
- Added new methods for cancelling confirmed bike and car policies
- Added `cancelConfirmedBike()` and `cancelConfirmedCar()` methods using PUT requests

**New Methods**:
```typescript
cancelConfirmedBike(id: number): Observable<void>
cancelConfirmedCar(id: number): Observable<void>
```

### 6. Vehicle Model (`vehicle.model.ts`)
**File**: `src/app/models/vehicle.model.ts`

**Changes**:
- Added `status` field to all policy DTOs to support status-based operations

**Updated Interfaces**:
```typescript
export interface BikeDTO {
  // ... existing fields
  status?: string; // PENDING, CONFIRMED, CANCELLED
}

export interface CarDTO {
  // ... existing fields
  status?: string; // PENDING, CONFIRMED, CANCELLED
}

export interface HealthInsuranceDTO {
  // ... existing fields
  status?: string; // PENDING, CONFIRMED, CANCELLED
}

export interface LifeInsuranceDTO {
  // ... existing fields
  status?: string; // PENDING, CONFIRMED, CANCELLED
}
```

### 7. My Purchases Component
**File**: `src/app/components/customer-dashboard/my-purchases/my-purchases.component.ts`

**Status**: Already implemented status-based cancellation
- No changes needed as the component already handles:
  - Status-based filtering (all, active, inactive)
  - Status-based cancellation (updates status to 'CANCELLED' instead of deleting)
  - Proper button enabling/disabling based on policy status

## Key Features Implemented

### 1. Unified Policy View
- Admin-created policies displayed in a single interface
- Consistent UI/UX across all policy types
- Unified filtering and search capabilities

### 2. User-Specific Policy Creation
- Users create their own instances of policies with their specific details
- Premium calculation based on user's details, not admin policy
- Each user has their own policy records

### 3. Status-Based Policy Management
- Policies show proper status (PENDING, CONFIRMED, CANCELLED)
- Cancelled policies cannot be renewed or edited
- Status-based filtering works correctly

### 4. Proper Cancellation Logic
- Cancellation before confirmation: Deletes entity from table
- Cancellation after confirmation: Updates status to 'CANCELLED'
- Consistent status updates across all policy types

### 5. Enhanced User Experience
- Simplified navigation (single "View Policies" button)
- Consistent policy creation flow for all types
- Proper status indicators and action controls

## Backend Integration

The frontend changes are designed to work with the backend changes outlined in `BACKEND_CHANGES_FOR_POLICY_CANCELLATION.md`. Key integration points:

1. **Admin Policy API**: Frontend expects `/api/customer/availablepolicies` endpoint
2. **Status Field**: All policy DTOs now include status field
3. **Cancellation Endpoints**: New PUT endpoints for cancelling confirmed policies
4. **Data Consistency**: Frontend expects backend to maintain status consistency
5. **Premium Calculation**: Backend should calculate premiums based on user details

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

## Testing Checklist

After implementing these changes, test:

1. **Policy View**:
   - [ ] Admin-created policies display correctly
   - [ ] Filtering by type works
   - [ ] Filtering by status works
   - [ ] "View Details" button navigates correctly

2. **Policy Creation**:
   - [ ] Can create bike policies with user details
   - [ ] Can create car policies with user details
   - [ ] Can create health policies with user details
   - [ ] Can create life policies with user details

3. **Premium Calculation**:
   - [ ] Premium calculation works for all policy types
   - [ ] Calculated premium is based on user details
   - [ ] Entity is created with PENDING status after calculation

4. **Policy Confirmation/Cancellation**:
   - [ ] Can confirm purchase (creates PolicyPurchase entry)
   - [ ] Can cancel before confirmation (deletes entity)
   - [ ] Can cancel after confirmation (updates status to CANCELLED)
   - [ ] Cancelled policies cannot be renewed/edited

5. **My Purchases**:
   - [ ] All policy types appear in purchases list
   - [ ] Status filtering works correctly
   - [ ] Cancellation updates status properly

## Files Modified

1. `src/app/components/customer-dashboard/customer-dashboard.component.html`
2. `src/app/app.routes.ts`
3. `src/app/components/customer-dashboard/policy-view/policy-view.component.ts`
4. `src/app/components/customer-dashboard/vehicle-management/vehicle-management.component.ts`
5. `src/app/services/vehicle.service.ts`
6. `src/app/models/vehicle.model.ts`

## Files Created

1. `BACKEND_CHANGES_FOR_POLICY_CANCELLATION.md`
2. `FRONTEND_CHANGES_SUMMARY.md`

## Next Steps

1. Implement the backend changes outlined in `BACKEND_CHANGES_FOR_POLICY_CANCELLATION.md`
2. Ensure the `/api/customer/availablepolicies` endpoint returns admin-created policies
3. Test the complete flow with all policy types
4. Verify that cancelled policies show correct status
5. Ensure all filtering and cancellation features work as expected
