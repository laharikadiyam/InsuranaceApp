import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ClaimService } from '../../../services/claim.service';
import { Claim } from '../../../models/claim.model';

@Component({
  selector: 'app-claim-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './claim-management.component.html',
  styleUrls: ['./claim-management.component.css']
})
export class ClaimManagementComponent implements OnInit {
  claims: Claim[] = [];
  isLoading = false;
  errorMessage = '';
  successMessage = '';
  selectedStatus = '';

  constructor(private claimService: ClaimService) {}

  ngOnInit(): void {
    this.loadClaims();
  }

  loadClaims(): void {
    this.isLoading = true;
    this.claimService.getAllClaims().subscribe({
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

  updateClaimStatus(claimId: number, status: string): void {
    this.isLoading = true;
    this.claimService.updateClaimStatus(claimId, status).subscribe({
      next: () => {
        this.successMessage = `Claim ${status.toLowerCase()} successfully.`;
        // Update the claim in the list
        this.claims = this.claims.map(claim => 
          this.getClaimId(claim) === claimId ? { ...claim, claimStatus: status } : claim
        );
        this.isLoading = false;
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: () => {
        this.errorMessage = 'Failed to update claim status.';
        this.isLoading = false;
        setTimeout(() => this.errorMessage = '', 3000);
      }
    });
  }

  approveClaim(claim: Claim): void {
    const claimId = this.getClaimId(claim);
    this.updateClaimStatus(claimId, 'APPROVED');
  }

  rejectClaim(claim: Claim): void {
    const claimId = this.getClaimId(claim);
    this.updateClaimStatus(claimId, 'REJECTED');
  }

  getClaimId(claim: Claim): number {
    return claim.claimId || (claim as any).id || 0;
  }

  getUserId(claim: Claim): number {
    return claim.userId || (claim.user?.id) || 0;
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

  getFilteredClaims(): Claim[] {
    if (!this.selectedStatus) {
      return this.claims;
    }
    return this.claims.filter(claim => 
      claim.claimStatus.toUpperCase() === this.selectedStatus.toUpperCase()
    );
  }
}
