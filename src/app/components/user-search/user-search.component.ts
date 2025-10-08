import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { UserProfileResponse, Roles } from '../../models/user.model';

@Component({
  selector: 'app-user-search',
  templateUrl: './user-search.component.html',
  styleUrls: ['./user-search.component.css'],
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  standalone: true
})
export class UserSearchComponent {
  searchForm: FormGroup;
  searchedUser: UserProfileResponse | null = null;
  isLoading = false;
  errorMessage = '';
  successMessage = '';
  isCurrentUserAdmin = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService
  ) {
    this.searchForm = this.fb.group({
      searchType: ['email', Validators.required],
      searchValue: ['', Validators.required]
    });
    this.checkCurrentUserRole();
  }

  private checkCurrentUserRole(): void {
    this.isCurrentUserAdmin = this.authService.isAdmin();
  }

  onSubmit(): void {
    if (this.searchForm.valid) {
      this.isLoading = true;
      this.errorMessage = '';
      this.successMessage = '';
      this.searchedUser = null;

      const formValue = this.searchForm.value;
      let email: string | undefined;
      let id: number | undefined;

      if (formValue.searchType === 'email') {
        email = formValue.searchValue;
      } else {
        id = parseInt(formValue.searchValue);
      }

      this.authService.findUser(email, id).subscribe({
        next: (user) => {
          this.isLoading = false;
          this.searchedUser = user;
          this.successMessage = 'User found successfully!';
        },
        error: (error) => {
          this.isLoading = false;
          this.errorMessage = error.error || 'User not found. Please check your search criteria.';
        }
      });
    }
  }

  clearSearch(): void {
    this.searchForm.reset({ searchType: 'email' });
    this.searchedUser = null;
    this.errorMessage = '';
    this.successMessage = '';
  }

  getStatusBadgeClass(isActive: boolean): string {
    return isActive ? 'badge bg-success' : 'badge bg-warning text-dark';
  }

  getStatusText(isActive: boolean): string {
    return isActive ? 'Active' : 'Inactive';
  }

  getRoleBadgeClass(role: string): string {
    return role === 'ADMIN' ? 'badge bg-danger' : 'badge bg-primary';
  }

  activateUser(): void {
    if (!this.searchedUser) return;

    // Check if user is already active
    if (this.searchedUser.isActive) {
      this.errorMessage = `This ${this.searchedUser.role.toLowerCase()} is already active!`;
      this.successMessage = '';
      // Clear error message after 3 seconds
      setTimeout(() => {
        this.errorMessage = '';
      }, 3000);
      return;
    }

    // Check if trying to activate current user
    const currentUser = this.authService.getCurrentUser();
    if (currentUser && currentUser.id === this.searchedUser.id) {
      this.errorMessage = 'You cannot activate your own account!';
      this.successMessage = '';
      // Clear error message after 3 seconds
      setTimeout(() => {
        this.errorMessage = '';
      }, 3000);
      return;
    }

    const action = this.searchedUser.role === Roles.ADMIN ? 
      this.authService.activateAdmin(this.searchedUser.id) : 
      this.authService.activateCustomer(this.searchedUser.id);

    action.subscribe({
      next: (response) => {
        // Show success message
        this.successMessage = response;
        // Clear any previous error messages
        this.errorMessage = '';
        // Refresh user data to get the actual backend state
        this.refreshUserData();
        // Clear success message after 3 seconds
        setTimeout(() => {
          this.successMessage = '';
        }, 3000);
      },
      error: (error) => {
        console.error('Activation error:', error);
        let errorMsg = 'Unknown error occurred';
        
        if (error.error) {
          if (typeof error.error === 'string') {
            errorMsg = error.error;
          } else if (error.error.message) {
            errorMsg = error.error.message;
          } else if (error.error.error) {
            errorMsg = error.error.error;
          }
        } else if (error.message) {
          errorMsg = error.message;
        }
        
        this.errorMessage = `Failed to activate ${this.searchedUser?.role.toLowerCase()}: ${errorMsg}`;
        this.successMessage = '';
        // Clear error message after 5 seconds
        setTimeout(() => {
          this.errorMessage = '';
        }, 5000);
      }
    });
  }

  deactivateUser(): void {
    if (!this.searchedUser) return;

    // Check if user is already inactive
    if (!this.searchedUser.isActive) {
      this.errorMessage = `This ${this.searchedUser.role.toLowerCase()} is already inactive!`;
      this.successMessage = '';
      // Clear error message after 3 seconds
      setTimeout(() => {
        this.errorMessage = '';
      }, 3000);
      return;
    }

    // Check if trying to deactivate current user
    const currentUser = this.authService.getCurrentUser();
    if (currentUser && currentUser.id === this.searchedUser.id) {
      this.errorMessage = 'You cannot deactivate your own account!';
      this.successMessage = '';
      // Clear error message after 3 seconds
      setTimeout(() => {
        this.errorMessage = '';
      }, 3000);
      return;
    }

    if (confirm(`Are you sure you want to deactivate this ${this.searchedUser.role.toLowerCase()}?`)) {
      this.authService.deactivateUser(this.searchedUser.id).subscribe({
        next: (response) => {
          // Show success message
          this.successMessage = response;
          // Clear any previous error messages
          this.errorMessage = '';
          // Refresh user data to get the actual backend state
          this.refreshUserData();
          // Clear success message after 3 seconds
          setTimeout(() => {
            this.successMessage = '';
          }, 3000);
        },
        error: (error) => {
          console.error('Deactivation error:', error);
          let errorMsg = 'Unknown error occurred';
          
          if (error.error) {
            if (typeof error.error === 'string') {
              errorMsg = error.error;
            } else if (error.error.message) {
              errorMsg = error.error.message;
            } else if (error.error.error) {
              errorMsg = error.error.error;
            }
          } else if (error.message) {
            errorMsg = error.message;
          }
          
          this.errorMessage = `Failed to deactivate user: ${errorMsg}`;
          this.successMessage = '';
          // Clear error message after 5 seconds
          setTimeout(() => {
            this.errorMessage = '';
          }, 3000);
        }
      });
    }
  }

  private refreshUserData(): void {
    if (!this.searchedUser) return;

    // Re-fetch the user data to get updated status from backend
    const email = this.searchedUser.email;
    const id = this.searchedUser.id;

    this.authService.findUser(email, id).subscribe({
      next: (user) => {
        this.searchedUser = user;
        console.log('User data refreshed:', user);
      },
      error: (error) => {
        console.error('Error refreshing user data:', error);
        // If refresh fails, we'll keep the current state
        // The user can manually refresh the page if needed
      }
    });
  }
}
