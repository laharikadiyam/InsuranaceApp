import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ClaimService } from '../../../services/claim.service';
import { PurchaseService } from '../../../services/purchase.service';
import { AuthService } from '../../../services/auth.service';
import { Claim, ClaimDTO } from '../../../models/claim.model';
import { PolicyPurchase } from '../../../models/purchase.model';

@Component({
  selector: 'app-claim',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './claim.component.html',
  styleUrls: ['./claim.component.css']
})
export class ClaimComponent implements OnInit {
  claims: Claim[] = [];
  purchases: PolicyPurchase[] = [];
  activePurchases: PolicyPurchase[] = []; // Only active policies for claims
  isLoading = false;
  errorMessage = '';
  successMessage = '';
  showClaimForm = false;
  claimForm: FormGroup;
  userId: number | null = null;

  constructor(
    private claimService: ClaimService,
    private purchaseService: PurchaseService,
    private authService: AuthService,
    private fb: FormBuilder
  ) {
    this.claimForm = this.fb.group({
      purchaseId: ['', Validators.required],
      claimStatus: ['PENDING', Validators.required]
    });
  }

  ngOnInit(): void {
    this.userId = this.authService.getCurrentUser()?.id || null;
    if (this.userId) {
      this.loadClaims();
      this.loadPurchases();
    }
  }

  loadClaims(): void {
    if (!this.userId) return;
    this.isLoading = true;
    this.claimService.getClaimsByUser(this.userId).subscribe({
      next: (data) => {
        this.claims = data;
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load claims.';
        this.isLoading = false;
      }
    });
  }

  loadPurchases(): void {
    if (!this.userId) return;
    this.purchaseService.getPurchasesByUser(this.userId).subscribe({
      next: (data) => {
        this.purchases = data;
        // Filter only active (CONFIRMED) policies for claims
        this.activePurchases = data.filter(purchase => 
          purchase.status === 'CONFIRMED' || purchase.status === 'ACTIVE'
        );
      },
      error: () => {
        this.errorMessage = 'Failed to load purchases.';
      }
    });
  }

  showNewClaimForm(): void {
    this.showClaimForm = true;
    this.claimForm.reset({ claimStatus: 'PENDING' });
  }

  hideClaimForm(): void {
    this.showClaimForm = false;
    this.claimForm.reset();
  }

  submitClaim(): void {
    if (this.claimForm.valid && this.userId) {
      const claimData: ClaimDTO = {
        userId: this.userId,
        purchaseId: this.claimForm.value.purchaseId,
        claimStatus: this.claimForm.value.claimStatus
      };

      this.isLoading = true;
      this.claimService.raiseClaim(claimData).subscribe({
        next: () => {
          this.successMessage = 'Claim submitted successfully.';
          this.hideClaimForm();
          this.loadClaims();
          this.isLoading = false;
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: () => {
          this.errorMessage = 'Failed to submit claim.';
          this.isLoading = false;
          setTimeout(() => this.errorMessage = '', 3000);
        }
      });
    }
  }

  withdrawClaim(claim: Claim): void {
    if (!confirm('Are you sure you want to withdraw this claim?')) return;
    
    const claimId = this.getClaimId(claim);
    this.isLoading = true;
    this.claimService.deleteClaim(claimId).subscribe({
      next: () => {
        this.successMessage = 'Claim withdrawn successfully.';
        this.claims = this.claims.filter(c => this.getClaimId(c) !== claimId);
        this.isLoading = false;
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: () => {
        this.errorMessage = 'Failed to withdraw claim.';
        this.isLoading = false;
        setTimeout(() => this.errorMessage = '', 3000);
      }
    });
  }

  getClaimId(claim: Claim): number {
    return claim.claimId || (claim as any).id || 0;
  }

  getPurchaseId(claim: Claim): number {
    return claim.purchaseId || (claim.purchase?.purchaseId) || 0;
  }

  getStatusBadgeClass(status: string): string {
    switch (status.toUpperCase()) {
      case 'PENDING':
        return 'badge bg-warning';
      case 'APPROVED':
        return 'badge bg-success';
      case 'REJECTED':
        return 'badge bg-danger';
      default:
        return 'badge bg-secondary';
    }
  }

  getPolicyType(purchase: PolicyPurchase): string {
    if (purchase.bikePolicy) return 'Bike';
    if (purchase.carPolicy) return 'Car';
    if (purchase.healthPolicy) return 'Health';
    if (purchase.lifePolicy) return 'Life';
    return 'Unknown';
  }

  getPolicyStatus(purchase: PolicyPurchase): string {
    return purchase.status || 'Unknown';
  }

  getPolicyStatusBadgeClass(status: string | undefined): string {
    switch (status?.toUpperCase()) {
      case 'CONFIRMED':
      case 'ACTIVE':
        return 'badge bg-success';
      case 'CANCELLED':
        return 'badge bg-danger';
      case 'EXPIRED':
        return 'badge bg-warning';
      case 'PENDING':
        return 'badge bg-info';
      default:
        return 'badge bg-secondary';
    }
  }

  hasActivePolicies(): boolean {
    return this.activePurchases.length > 0;
  }
}
