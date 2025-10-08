import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { UserProfileResponse } from '../../models/user.model';

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css'],
  imports: [CommonModule, RouterModule],
  standalone: true
})
export class AdminDashboardComponent implements OnInit {
  currentUser: UserProfileResponse | null = null;
  pendingAdmins: UserProfileResponse[] = [];
  pendingCustomers: UserProfileResponse[] = [];
  isLoading = false;
  errorMessage = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadAdminProfile();
    this.loadPendingUsers();
  }

  loadAdminProfile(): void {
    this.authService.getAdminProfile().subscribe({
      next: (profile) => {
        console.log('Admin profile loaded:', profile);
        console.log('isActive value:', profile.isActive);
        console.log('isActive type:', typeof profile.isActive);
        this.currentUser = profile;
      },
      error: (error) => {
        this.errorMessage = 'Failed to load profile';
        console.error('Error loading admin profile:', error);
      }
    });
  }

  loadPendingUsers(): void {
    this.isLoading = true;
    
    // Load pending admins
    this.authService.getPendingAdmins().subscribe({
      next: (admins) => {
        this.pendingAdmins = admins;
      },
      error: (error) => {
        console.error('Error loading pending admins:', error);
      }
    });

    // Load pending customers
    this.authService.getPendingCustomers().subscribe({
      next: (customers) => {
        this.pendingCustomers = customers;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading pending customers:', error);
        this.isLoading = false;
      }
    });
  }

  activateAdmin(adminId: number): void {
    this.authService.activateAdmin(adminId).subscribe({
      next: (response) => {
        alert(response);
        this.loadPendingUsers(); // Refresh the list
      },
      error: (error) => {
        alert('Failed to activate admin: ' + (error.error || 'Unknown error'));
      }
    });
  }

  activateCustomer(customerId: number): void {
    this.authService.activateCustomer(customerId).subscribe({
      next: (response) => {
        alert(response);
        this.loadPendingUsers(); // Refresh the list
      },
      error: (error) => {
        alert('Failed to activate customer: ' + (error.error || 'Unknown error'));
      }
    });
  }

  deactivateUser(userId: number): void {
    if (confirm('Are you sure you want to deactivate this user?')) {
      this.authService.deactivateUser(userId).subscribe({
        next: (response) => {
          alert(response);
          this.loadPendingUsers(); // Refresh the list
        },
        error: (error) => {
          alert('Failed to deactivate user: ' + (error.error || 'Unknown error'));
        }
      });
    }
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  getStatusBadgeClass(isActive: any): string {
    const isActiveBool = isActive === true || isActive === 'true';
    return isActiveBool ? 'badge bg-success' : 'badge bg-warning text-dark';
  }

  getStatusText(isActive: boolean): string {
    console.log('getStatusText called with:', isActive, 'type:', typeof isActive);
    return isActive ? 'Active' : 'Inactive';
  }
}
