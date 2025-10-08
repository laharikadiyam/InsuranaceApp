import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HealthInsuranceService, HealthInsuranceDTO } from '../../../services/health-insurance.service';
import { PurchaseService } from '../../../services/purchase.service';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-health-insurance',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './health-insurance.component.html',
  styleUrls: ['./health-insurance.component.css']
})
export class HealthInsuranceComponent implements OnInit {
  healthPolicies: HealthInsuranceDTO[] = [];
  isLoading = false;
  errorMessage = '';
  successMessage = '';
  showForm = false;
  healthForm: FormGroup;
  userId: number | null = null;
  calculatedPremium: number | null = null;

  constructor(
    private healthService: HealthInsuranceService,
    private purchaseService: PurchaseService,
    private authService: AuthService,
    private fb: FormBuilder
  ) {
    this.healthForm = this.fb.group({
      age: ['', [Validators.required, Validators.min(1), Validators.max(100)]],
      numberOfMembers: ['', [Validators.required, Validators.min(1), Validators.max(10)]],
      sumInsured: ['', [Validators.required, Validators.min(10000), Validators.max(10000000)]],
      smoker: [false],
      preExisting: [false]
    });
  }

  ngOnInit(): void {
    this.userId = this.authService.getCurrentUser()?.id || null;
    if (this.userId) {
      this.loadHealthPolicies();
    }
  }

  loadHealthPolicies(): void {
    this.isLoading = true;
    this.healthService.getAll().subscribe({
      next: (data) => {
        this.healthPolicies = data;
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load health policies.';
        this.isLoading = false;
      }
    });
  }

  showNewPolicyForm(): void {
    this.showForm = true;
    this.healthForm.reset();
    this.calculatedPremium = null;
  }

  hideForm(): void {
    this.showForm = false;
    this.healthForm.reset();
    this.calculatedPremium = null;
  }

  calculatePremium(): void {
    if (this.healthForm.valid && this.userId) {
      const formData = this.healthForm.value;
      const dto: HealthInsuranceDTO = {
        ...formData,
        user_id: this.userId
      };

      this.healthService.calculatePremium(dto).subscribe({
        next: (premium) => {
          this.calculatedPremium = premium;
        },
        error: () => {
          this.errorMessage = 'Failed to calculate premium.';
        }
      });
    }
  }

  submitPolicy(): void {
    if (this.healthForm.valid && this.userId) {
      const formData = this.healthForm.value;
      const dto: HealthInsuranceDTO = {
        ...formData,
        user_id: this.userId
      };

      this.isLoading = true;
      this.healthService.addHealth(dto).subscribe({
        next: (policy) => {
          this.successMessage = 'Health policy created successfully.';
          this.healthPolicies.push(policy);
          this.hideForm();
          this.isLoading = false;
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: () => {
          this.errorMessage = 'Failed to create health policy.';
          this.isLoading = false;
          setTimeout(() => this.errorMessage = '', 3000);
        }
      });
    }
  }

  confirmPurchase(policy: HealthInsuranceDTO): void {
    if (!this.userId || !policy.id) return;

    const request = {
      userId: this.userId,
      healthPolicyId: policy.id,
      purchaseDate: new Date().toISOString().substring(0, 10),
      expiryDate: new Date(Date.now() + 365 * 24 * 60 * 60 * 1000).toISOString().substring(0, 10)
    };

    this.isLoading = true;
    this.healthService.confirmPurchase(policy.id, request).subscribe({
      next: (confirmedPolicy) => {
        this.successMessage = 'Health policy purchased successfully.';
        this.healthPolicies = this.healthPolicies.map(p => 
          p.id === policy.id ? confirmedPolicy : p
        );
        this.isLoading = false;
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: () => {
        this.errorMessage = 'Failed to purchase health policy.';
        this.isLoading = false;
        setTimeout(() => this.errorMessage = '', 3000);
      }
    });
  }

  cancelPolicy(policyId: number): void {
    if (!confirm('Are you sure you want to cancel this policy?')) return;

    this.isLoading = true;
    this.healthService.cancelPendingHealth(policyId).subscribe({
      next: () => {
        this.successMessage = 'Policy cancelled successfully.';
        this.healthPolicies = this.healthPolicies.filter(p => p.id !== policyId);
        this.isLoading = false;
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: () => {
        this.errorMessage = 'Failed to cancel policy.';
        this.isLoading = false;
        setTimeout(() => this.errorMessage = '', 3000);
      }
    });
  }

  getStatusBadgeClass(status: string | undefined): string {
    switch (status?.toUpperCase()) {
      case 'PENDING':
        return 'badge bg-warning';
      case 'CONFIRMED':
        return 'badge bg-success';
      case 'CANCELLED':
        return 'badge bg-danger';
      default:
        return 'badge bg-secondary';
    }
  }

  canConfirm(policy: HealthInsuranceDTO): boolean {
    return policy.status === 'PENDING' || !policy.status;
  }

  canCancel(policy: HealthInsuranceDTO): boolean {
    return policy.status === 'PENDING' || !policy.status;
  }
}
