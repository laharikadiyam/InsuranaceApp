import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { UserProfileResponse } from '../../../models/user.model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-pending-customers.component',
  imports: [CommonModule, RouterModule],
  templateUrl: './pending-customers.component.html',
  styleUrl: './pending-customers.component.css',
  standalone: true
})
export class PendingCustomersComponent implements OnInit {
  pendingCustomers: UserProfileResponse[] = [];
  isLoading = false;
  errorMessage = '';

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    this.loadPendingCustomers();
  }

  loadPendingCustomers(): void {
    this.isLoading = true;
    this.authService.getPendingCustomers().subscribe({
      next: (customers) => {
        this.pendingCustomers = customers;
        this.isLoading = false;
      },
      error: (error) => {
        this.errorMessage = 'Failed to load pending customers';
        this.isLoading = false;
        console.error('Error loading pending customers:', error);
      }
    });
  }

  activateCustomer(customerId: number): void {
    this.authService.activateCustomer(customerId).subscribe({
      next: (response) => {
        alert(response);
        this.loadPendingCustomers(); // Refresh the list
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
          this.loadPendingCustomers(); // Refresh the list
        },
                error: (error) => {
          alert('Failed to deactivate user: ' + (error.error || 'Unknown error'));
        }
      });
    }
  }

  goBack(): void {
    this.router.navigate(['/admin/dashboard']);
  }

  getStatusBadgeClass(isActive: boolean): string {
    return isActive ? 'badge bg-success' : 'badge bg-warning text-dark';
  }

  getStatusText(isActive: boolean): string {
    return isActive ? 'Active' : 'Inactive';
  }
}
