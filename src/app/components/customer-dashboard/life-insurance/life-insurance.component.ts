import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { LifeInsuranceService, LifeInsuranceDTO } from '../../../services/life-insurance.service';
import { PurchaseService } from '../../../services/purchase.service';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-life-insurance',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './life-insurance.component.html',
  styleUrls: ['./life-insurance.component.css']
})
export class LifeInsuranceComponent implements OnInit {
  lifePolicies: LifeInsuranceDTO[] = [];
  isLoading = false;
  errorMessage = '';
  successMessage = '';
  showForm = false;
  lifeForm: FormGroup;
  userId: number | null = null;
  calculatedPremium: number | null = null;

  occupationRisks = [
    { value: 'low', label: 'Low Risk' },
    { value: 'medium', label: 'Medium Risk' },
    { value: 'high', label: 'High Risk' }
  ];

  genders = [
    { value: 'male', label: 'Male' },
    { value: 'female', label: 'Female' }
  ];

  constructor(
    private lifeService: LifeInsuranceService,
    private purchaseService: PurchaseService,
    private authService: AuthService,
    private fb: FormBuilder
  ) {
    this.lifeForm = this.fb.group({
      age: ['', [Validators.required, Validators.min(18), Validators.max(70)]],
      gender: ['', Validators.required],
      sumAssured: ['', [Validators.required, Validators.min(100000), Validators.max(10000000)]],
      policyTerm: ['', [Validators.required, Validators.min(5), Validators.max(30)]],
      smoker: [false],
      occupationRisk: ['low', Validators.required]
    });
  }

  ngOnInit(): void {
    this.userId = this.authService.getCurrentUser()?.id || null;
    if (this.userId) {
      this.loadLifePolicies();
    }
  }

  loadLifePolicies(): void {
    this.isLoading = true;
    this.lifeService.getAll().subscribe({
      next: (data) => {
        this.lifePolicies = data;
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load life policies.';
        this.isLoading = false;
      }
    });
  }

  showNewPolicyForm(): void {
    this.showForm = true;
    this.lifeForm.reset({ occupationRisk: 'low' });
    this.calculatedPremium = null;
  }

  hideForm(): void {
    this.showForm = false;
    this.lifeForm.reset();
    this.calculatedPremium = null;
  }

  calculatePremium(): void {
    if (this.lifeForm.valid && this.userId) {
      const formData = this.lifeForm.value;
      const dto: LifeInsuranceDTO = {
        ...formData,
        user_id: this.userId
      };

      this.lifeService.calculatePremium(dto).subscribe({
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
    if (this.lifeForm.valid && this.userId) {
      const formData = this.lifeForm.value;
      const dto: LifeInsuranceDTO = {
        ...formData,
        user_id: this.userId
      };

      this.isLoading = true;
      this.lifeService.addLife(dto).subscribe({
        next: (policy) => {
          this.successMessage = 'Life policy created successfully.';
          this.lifePolicies.push(policy);
          this.hideForm();
          this.isLoading = false;
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: () => {
          this.errorMessage = 'Failed to create life policy.';
          this.isLoading = false;
          setTimeout(() => this.errorMessage = '', 3000);
        }
      });
    }
  }

  confirmPurchase(policy: LifeInsuranceDTO): void {
    if (!this.userId || !policy.id) return;

    const request = {
      userId: this.userId,
      lifePolicyId: policy.id,
      purchaseDate: new Date().toISOString().substring(0, 10),
      expiryDate: new Date(Date.now() + 365 * 24 * 60 * 60 * 1000).toISOString().substring(0, 10)
    };

    this.isLoading = true;
    this.lifeService.confirmPurchase(policy.id, request).subscribe({
      next: (confirmedPolicy) => {
        this.successMessage = 'Life policy purchased successfully.';
        this.lifePolicies = this.lifePolicies.map(p => 
          p.id === policy.id ? confirmedPolicy : p
        );
        this.isLoading = false;
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: () => {
        this.errorMessage = 'Failed to purchase life policy.';
        this.isLoading = false;
        setTimeout(() => this.errorMessage = '', 3000);
      }
    });
  }

  cancelPolicy(policyId: number): void {
    if (!confirm('Are you sure you want to cancel this policy?')) return;

    this.isLoading = true;
    this.lifeService.cancelPendingLife(policyId).subscribe({
      next: () => {
        this.successMessage = 'Policy cancelled successfully.';
        this.lifePolicies = this.lifePolicies.filter(p => p.id !== policyId);
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

  canConfirm(policy: LifeInsuranceDTO): boolean {
    return policy.status === 'PENDING' || !policy.status;
  }

  canCancel(policy: LifeInsuranceDTO): boolean {
    return policy.status === 'PENDING' || !policy.status;
  }
}
