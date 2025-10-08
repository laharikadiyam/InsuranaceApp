# Policy Management Module

This document describes the policy management features that have been added to the Angular Insurance App.

## Overview

The policy management module allows administrators to manage insurance policies and customers to view available policies. The module integrates seamlessly with the existing Angular application structure and follows the same design patterns.

## Features

### Admin Features
- **Create Policies**: Add new insurance policies with details like name, type, premium, tenure, and coverage
- **Edit Policies**: Modify existing policy details
- **Delete Policies**: Remove policies from the system
- **View All Policies**: See a comprehensive list of all policies with filtering options
- **Filter Policies**: Filter by policy type and active status
- **Policy Status Management**: Toggle policies between active and inactive states

### Customer Features
- **View Available Policies**: Browse all available insurance policies
- **Filter Policies**: Search and filter policies by type and status
- **Policy Details**: View comprehensive policy information including coverage details

## Components

### 1. Policy Management Component (`policy-management.component`)
**Location**: `src/app/components/admin-dashboard/policy-management/`

**Features**:
- Full CRUD operations for policies
- Form validation with real-time feedback
- Responsive table layout
- Advanced filtering capabilities
- Success/error message handling

**Key Methods**:
- `loadPolicies()`: Fetches policies from the backend
- `createPolicy()`: Creates new policies
- `updatePolicy()`: Updates existing policies
- `deletePolicy()`: Removes policies
- `applyFilters()`: Applies search filters

### 2. Policy View Component (`policy-view.component`)
**Location**: `src/app/components/customer-dashboard/policy-view/`

**Features**:
- Card-based policy display
- Search and filter functionality
- Responsive grid layout
- Policy status indicators

## Services

### Policy Service (`policy.service.ts`)
**Location**: `src/app/services/policy.service.ts`

**API Endpoints**:
- Admin endpoints (require ADMIN role):
  - `POST /api/admin/policies` - Create policy
  - `PUT /api/admin/policies/{id}` - Update policy
  - `GET /api/admin/policies/{id}` - Get policy by ID
  - `GET /api/admin/policies` - Get all policies with filters
  - `DELETE /api/admin/policies/{id}` - Delete policy

- Customer endpoints (require CUSTOMER role):
  - `GET /api/customer/availablepolicies` - Get available policies

**Features**:
- Automatic JWT token inclusion for authentication
- Query parameter support for filtering
- Error handling and logging

## Models

### Policy Models (`policy.model.ts`)
**Location**: `src/app/models/policy.model.ts`

**Interfaces**:
- `Policy`: Core policy entity
- `PolicyDto`: Data transfer object for API communication
- `CreatePolicyRequest`: Request model for creating policies
- `UpdatePolicyRequest`: Request model for updating policies

## Routes

### Admin Routes
- `/admin/dashboard/policy-management` - Policy management interface

### Customer Routes
- `/customer/dashboard/policy-view` - Policy viewing interface

## Integration

### Admin Dashboard Integration
The policy management feature is integrated into the admin dashboard with:
- New "Manage Policies" button in the quick actions section
- Consistent styling with existing dashboard components
- Proper route protection with AdminGuard

### Customer Dashboard Integration
The policy viewing feature is integrated into the customer dashboard with:
- "View Policies" button in the quick actions section
- Consistent styling with existing dashboard components
- Proper route protection with CustomerGuard

## Styling

Both components use:
- Bootstrap 5 classes for responsive design
- Custom CSS that matches the existing application theme
- Hover effects and transitions for better UX
- Mobile-responsive layouts

## Security

- All API calls include JWT authentication headers
- Route protection with role-based guards
- Form validation on both client and server side
- Proper error handling and user feedback

## Usage

### For Administrators
1. Navigate to Admin Dashboard
2. Click "Manage Policies" in the quick actions
3. Use the interface to create, edit, or delete policies
4. Apply filters to find specific policies

### For Customers
1. Navigate to Customer Dashboard
2. Click "View Policies" in the quick actions
3. Browse available policies
4. Use filters to find specific policy types

## Backend Integration

The module is designed to work with the provided Java Spring Boot backend that includes:
- Policy entity and repository
- Policy service with CRUD operations
- Policy controller with admin and customer endpoints
- Proper validation and error handling

## Future Enhancements

Potential improvements could include:
- Policy comparison features
- Policy recommendation engine
- Advanced search with multiple criteria
- Policy templates for quick creation
- Bulk operations for policy management
- Policy analytics and reporting









