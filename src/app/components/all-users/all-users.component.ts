import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { UserProfileResponse, Roles } from '../../models/user.model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-all-users',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule],
  templateUrl: './all-users.component.html',
  styleUrl: './all-users.component.css'
})
export class AllUsersComponent implements OnInit {
  allUsers: UserProfileResponse[] = [];
  filteredUsers: UserProfileResponse[] = [];
  isLoading = false;
  errorMessage = '';
  filterForm: FormGroup;
  selectedRole: string = 'ALL';

  constructor(
    private authService: AuthService,
    private router: Router,
    private fb: FormBuilder
  ) {
    this.filterForm = this.fb.group({
      roleFilter: ['ALL']
    });
  }

  ngOnInit(): void {
    this.loadAllUsers();
    this.setupFilterListener();
  }

  setupFilterListener(): void {
    this.filterForm.get('roleFilter')?.valueChanges.subscribe(role => {
      this.selectedRole = role;
      this.filterUsers();
    });
  }

  loadAllUsers(): void {
    this.isLoading = true;
    this.authService.getAllUsers().subscribe({
      next: (users) => {
        this.allUsers = users;
        this.filteredUsers = users;
        this.isLoading = false;
      },
      error: (error) => {
        this.errorMessage = 'Failed to load users';
        this.isLoading = false;
        console.error('Error loading users:', error);
      }
    });
  }

  filterUsers(): void {
    if (this.selectedRole === 'ALL') {
      this.filteredUsers = this.allUsers;
    } else {
      this.filteredUsers = this.allUsers.filter(user => user.role === this.selectedRole);
    }
  }

  activateUser(user: UserProfileResponse): void {
    const action = user.role === Roles.ADMIN ? 
      this.authService.activateAdmin(user.id) : 
      this.authService.activateCustomer(user.id);

    action.subscribe({
      next: (response) => {
        alert(response);
        this.loadAllUsers(); // Refresh the list
      },
      error: (error) => {
        alert(`Failed to activate ${user.role.toLowerCase()}: ` + (error.error || 'Unknown error'));
      }
    });
  }

  deactivateUser(user: UserProfileResponse): void {
    if (confirm(`Are you sure you want to deactivate this ${user.role.toLowerCase()}?`)) {
      this.authService.deactivateUser(user.id).subscribe({
        next: (response) => {
          alert(response);
          this.loadAllUsers(); // Refresh the list
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

  getRoleBadgeClass(role: string): string {
    return role === Roles.ADMIN ? 'badge bg-primary' : 'badge bg-info';
  }
}
