import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { UserProfileResponse } from '../../models/user.model';

@Component({
  selector: 'app-customer-dashboard',
  templateUrl: './customer-dashboard.component.html',
  styleUrls: ['./customer-dashboard.component.css'],
  imports: [CommonModule, RouterModule],
  standalone: true
})
export class CustomerDashboardComponent implements OnInit {
  currentUser: UserProfileResponse | null = null;
  isLoading = false;
  errorMessage = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadCustomerProfile();
  }

  loadCustomerProfile(): void {
    this.isLoading = true;
    this.authService.getCustomerProfile().subscribe({
      next: (profile) => {
        console.log('Customer profile loaded:', profile);
        console.log('isActive value:', profile.isActive);
        console.log('isActive type:', typeof profile.isActive);
        this.currentUser = profile;
        this.isLoading = false;
      },
      error: (error) => {
        this.errorMessage = 'Failed to load profile';
        this.isLoading = false;
        console.error('Error loading customer profile:', error);
      }
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  getStatusBadgeClass(isActive: boolean): string {
    console.log('getStatusBadgeClass called with:', isActive, 'type:', typeof isActive);
    return isActive ? 'badge bg-success' : 'badge bg-warning text-dark';
  }

  getStatusText(isActive: boolean): string {
    console.log('getStatusText called with:', isActive, 'type:', typeof isActive);
    return isActive ? 'Active' : 'Inactive';
  }
}
