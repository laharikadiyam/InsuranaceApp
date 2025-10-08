import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { Roles } from '../../models/user.model';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  standalone: true
})
export class LoginComponent {
  loginForm: FormGroup;
  isLoading = false;
  errorMessage = '';
  selectedRole: Roles = Roles.CUSTOMER;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  onSubmit(): void {
    if (this.loginForm.valid) {
      this.isLoading = true;
      this.errorMessage = '';

      const loginRequest = this.loginForm.value;

      const loginObservable = this.selectedRole === Roles.ADMIN 
        ? this.authService.loginAdmin(loginRequest)
        : this.authService.loginCustomer(loginRequest);

      loginObservable.subscribe({
        next: (response) => {
          this.isLoading = false;
          if (response.role === Roles.ADMIN) {
            this.router.navigate(['/admin/dashboard']);
          } else {
            this.router.navigate(['/customer/dashboard']);
          }
        },
        error: (error) => {
          this.isLoading = false;
          this.errorMessage = error.error || 'Login failed. Please try again.';
        }
      });
    }
  }

  onRoleChange(role: Roles): void {
    this.selectedRole = role;
  }

  getRoles(): typeof Roles {
    return Roles;
  }
}
