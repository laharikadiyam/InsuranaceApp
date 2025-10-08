import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { PurchaseService } from '../../../services/purchase.service';
import { AuthService } from '../../../services/auth.service';
import { PolicyPurchase } from '../../../models/purchase.model';

@Component({
  selector: 'app-my-purchases',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './my-purchases.component.html',
  styleUrls: ['./my-purchases.component.css']
})
export class MyPurchasesComponent implements OnInit {
  purchases: PolicyPurchase[] = [];
  filteredPurchases: PolicyPurchase[] = [];
  isLoading = false;
  errorMessage = '';
  successMessage = '';
  userId: number | null = null;
  filterType: 'all' | 'active' | 'inactive' = 'all';

  constructor(
    private purchaseService: PurchaseService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.userId = this.authService.getCurrentUser()?.id || null;
    if (this.userId) {
      this.loadPurchases();
    }
  }

  loadPurchases(): void {
    this.isLoading = true;
    let activeOnly: boolean | undefined;
    
    if (this.filterType === 'active') {
      activeOnly = true;
    } else if (this.filterType === 'inactive') {
      activeOnly = false;
    }
    
    this.purchaseService.getPurchasesByUser(this.userId!, activeOnly).subscribe({
      next: (data) => {
        this.purchases = data;
        this.filteredPurchases = data;
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load purchases.';
        this.isLoading = false;
      }
    });
  }

  onFilterChange(): void {
    this.loadPurchases();
  }

  cancelPurchase(purchaseId: number): void {
    if (!confirm('Are you sure you want to cancel this purchase?')) return;
    this.isLoading = true;
    this.purchaseService.cancelPurchase(purchaseId).subscribe({
      next: () => {
        this.successMessage = 'Purchase cancelled successfully.';
        // Update the status instead of removing from list
        this.purchases = this.purchases.map(p => 
          p.purchaseId === purchaseId ? { ...p, status: 'CANCELLED' } : p
        );
        this.filteredPurchases = this.filteredPurchases.map(p => 
          p.purchaseId === purchaseId ? { ...p, status: 'CANCELLED' } : p
        );
        this.isLoading = false;
        setTimeout(() => this.successMessage = '', 2000);
      },
      error: () => {
        this.errorMessage = 'Failed to cancel purchase.';
        this.isLoading = false;
      }
    });
  }

  renewPurchase(purchase: PolicyPurchase): void {
    if (!this.userId) return;
    const currentExpiry = new Date(purchase.expiryDate);
    const newExpiry = new Date(currentExpiry);
    newExpiry.setFullYear(currentExpiry.getFullYear() + 1);

    const req: any = {
      userId: this.userId,
      purchaseDate: purchase.purchaseDate, // keep original
      expiryDate: newExpiry.toISOString().substring(0, 10)
      // Do not send bike/car/health/life ids to avoid re-linking
    };

    this.isLoading = true;
    this.purchaseService.updatePurchase(purchase.purchaseId, req).subscribe({
      next: () => {
        this.successMessage = 'Policy renewed for 1 year.';
        // Update local list optimistically
        this.purchases = this.purchases.map(p => p.purchaseId === purchase.purchaseId ? { ...p, expiryDate: req.expiryDate } : p);
        this.filteredPurchases = this.filteredPurchases.map(p => p.purchaseId === purchase.purchaseId ? { ...p, expiryDate: req.expiryDate } : p);
        this.isLoading = false;
        setTimeout(() => this.successMessage = '', 2000);
      },
      error: () => {
        this.errorMessage = 'Failed to renew policy.';
        this.isLoading = false;
      }
    });
  }

  isPolicyActive(purchase: PolicyPurchase): boolean {
    if (purchase.status === 'CANCELLED') return false;
    const expiryDate = new Date(purchase.expiryDate);
    return expiryDate > new Date();
  }

  canRenew(purchase: PolicyPurchase): boolean {
    return this.isPolicyActive(purchase) && purchase.status !== 'CANCELLED';
  }

  canCancel(purchase: PolicyPurchase): boolean {
    return this.isPolicyActive(purchase) && purchase.status !== 'CANCELLED';
  }

  getPolicyStatus(purchase: PolicyPurchase): string {
    if (purchase.status === 'CANCELLED') return 'CANCELLED';
    const expiryDate = new Date(purchase.expiryDate);
    return expiryDate > new Date() ? 'ACTIVE' : 'EXPIRED';
  }
}
