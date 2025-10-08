import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.css'],
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  standalone: true
})
export class ChangePasswordComponent {
  changePasswordForm: FormGroup;
  isLoading = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.changePasswordForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      oldPassword: ['', [Validators.required]],
      newPassword: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]]
    }, { validators: this.passwordMatchValidator });
    
    // Auto-populate email if user is logged in
    const currentUser = this.authService.getCurrentUser();
    if (currentUser) {
      this.changePasswordForm.patchValue({
        email: currentUser.email
      });
      // Disable email field since it's auto-populated
      this.changePasswordForm.get('email')?.disable();
    }
  }

  passwordMatchValidator(form: FormGroup) {
    const newPassword = form.get('newPassword');
    const confirmPassword = form.get('confirmPassword');
    
    if (newPassword && confirmPassword && newPassword.value !== confirmPassword.value) {
      confirmPassword.setErrors({ passwordMismatch: true });
      return { passwordMismatch: true };
    }
    
    if (confirmPassword && confirmPassword.errors?.['passwordMismatch']) {
      delete confirmPassword.errors['passwordMismatch'];
      if (Object.keys(confirmPassword.errors).length === 0) {
        confirmPassword.setErrors(null);
      }
    }
    
    return null;
  }

  onSubmit(): void {
    if (this.changePasswordForm.valid) {
      this.isLoading = true;
      this.errorMessage = '';
      this.successMessage = '';

      const formValue = this.changePasswordForm.value;
      const changePasswordRequest = {
        email: formValue.email || this.changePasswordForm.get('email')?.value,
        oldPassword: formValue.oldPassword,
        newPassword: formValue.newPassword
      };

      this.authService.changePassword(changePasswordRequest).subscribe({
        next: (response) => {
          this.isLoading = false;
          this.successMessage = response;
          this.changePasswordForm.reset();
          setTimeout(() => {
            this.router.navigate(['/login']);
          }, 3000);
        },
        error: (error) => {
          this.isLoading = false;
          this.errorMessage = error.error || 'Password change failed. Please try again.';
        }
      });
    }
  }

  goBack(): void {
    const currentUser = this.authService.getCurrentUser();
    if (currentUser) {
      if (currentUser.role === 'ADMIN') {
        this.router.navigate(['/admin/dashboard']);
      } else {
        this.router.navigate(['/customer/dashboard']);
      }
    } else {
      this.router.navigate(['/login']);
    }
  }
}
