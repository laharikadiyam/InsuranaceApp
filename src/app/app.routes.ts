import { Routes } from '@angular/router';
import { AuthGuard, AdminGuard, CustomerGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { 
    path: 'login', 
    loadComponent: () => import('./components/login/login.component').then(m => m.LoginComponent)
  },
  { 
    path: 'register', 
    loadComponent: () => import('./components/register/register.component').then(m => m.RegisterComponent)
  },
  { 
    path: 'forgot-password', 
    loadComponent: () => import('./components/forgot-password/forgot-password.component').then(m => m.ForgotPasswordComponent)
  },
  { 
    path: 'admin/dashboard', 
    loadComponent: () => import('./components/admin-dashboard/admin-dashboard.component').then(m => m.AdminDashboardComponent),
    canActivate: [AdminGuard]
  },
  { 
    path: 'admin/dashboard/pending-admins', 
    loadComponent: () => import('./components/admin-dashboard/pending-admins/pending-admins.component').then(m => m.PendingAdminsComponent),
    canActivate: [AdminGuard]
  },
  { 
    path: 'admin/dashboard/pending-customers', 
    loadComponent: () => import('./components/admin-dashboard/pending-customers/pending-customers.component').then(m => m.PendingCustomersComponent),
    canActivate: [AdminGuard]
  },
  { 
    path: 'admin/dashboard/all-users', 
    loadComponent: () => import('./components/all-users/all-users.component').then(m => m.AllUsersComponent),
    canActivate: [AdminGuard]
  },
  { 
    path: 'admin/dashboard/policy-management', 
    loadComponent: () => import('./components/admin-dashboard/policy-management/policy-management.component').then(m => m.PolicyManagementComponent),
    canActivate: [AdminGuard]
  },
  { 
    path: 'admin/dashboard/claim-management', 
    loadComponent: () => import('./components/admin-dashboard/claim-management/claim-management.component').then(m => m.ClaimManagementComponent),
    canActivate: [AdminGuard]
  },
  { 
    path: 'customer/dashboard', 
    loadComponent: () => import('./components/customer-dashboard/customer-dashboard.component').then(m => m.CustomerDashboardComponent),
    canActivate: [CustomerGuard]
  },
  { 
    path: 'customer/dashboard/change-password', 
    loadComponent: () => import('./components/change-password/change-password.component').then(m => m.ChangePasswordComponent),
    canActivate: [CustomerGuard]
  },
  { 
    path: 'customer/dashboard/policy-view', 
    loadComponent: () => import('./components/customer-dashboard/policy-view/policy-view.component').then(m => m.PolicyViewComponent),
    canActivate: [CustomerGuard]
  },
  { 
    path: 'customer/dashboard/vehicle-management', 
    loadComponent: () => import('./components/customer-dashboard/vehicle-management/vehicle-management.component').then(m => m.VehicleManagementComponent),
    canActivate: [CustomerGuard]
  },
  { 
    path: 'customer/dashboard/purchase', 
    loadComponent: () => import('./components/customer-dashboard/purchase/purchase.component').then(m => m.PurchaseComponent),
    canActivate: [CustomerGuard]
  },
  { 
    path: 'customer/dashboard/my-purchases',
    loadComponent: () => import('./components/customer-dashboard/my-purchases/my-purchases.component').then(m => m.MyPurchasesComponent),
    canActivate: [CustomerGuard]
  },
  { 
    path: 'customer/dashboard/claim', 
    loadComponent: () => import('./components/customer-dashboard/claim/claim.component').then(m => m.ClaimComponent),
    canActivate: [CustomerGuard]
  },
  { path: '**', redirectTo: '/login' }
];
