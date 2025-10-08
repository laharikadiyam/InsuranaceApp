import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { UserProfileResponse } from '../../../models/user.model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-pending-admins.component',
  standalone: true,
  imports: [CommonModule, RouterModule], 
  templateUrl: './pending-admins.component.html',
  styleUrl: './pending-admins.component.css'
})
export class PendingAdminsComponent implements OnInit{
  pendingAdmins: UserProfileResponse[] = [];
  isLoading = false;
  errorMessage = '';

  constructor(private router: Router,private authService: AuthService) {}

  ngOnInit(): void {
    this.loadPendingAdmins();
  }

  loadPendingAdmins(): void {
    this.isLoading = true;
    this.authService.getPendingAdmins().subscribe({
      next: (admins) => {
        this.pendingAdmins = admins;
        this.isLoading = false;
      },
      error: (error) => {
        this.errorMessage = 'Failed to load pending admins';
        this.isLoading = false;
        console.error('Error loading pending admins:', error);
      }
    });
  }

  activateAdmin(adminId: number): void {
    this.authService.activateAdmin(adminId).subscribe({
      next: (response) => {
        alert(response);
        this.loadPendingAdmins(); // Refresh the list
      },
      error: (error) => {
        alert('Failed to activate admin: ' + (error.error || 'Unknown error'));
      }
    });
  }

  deactivateUser(userId: number): void {
    if (confirm('Are you sure you want to deactivate this user?')) {
      this.authService.deactivateUser(userId).subscribe({
        next: (response) => {
          alert(response);
          this.loadPendingAdmins(); // Refresh the list
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
