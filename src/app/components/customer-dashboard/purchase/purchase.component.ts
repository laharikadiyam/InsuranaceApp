import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { PurchaseService } from '../../../services/purchase.service';
import { AuthService } from '../../../services/auth.service';
import { PolicyPurchase } from '../../../models/purchase.model';
import { FormsModule } from '@angular/forms';
import { VehicleService } from '../../../services/vehicle.service';

@Component({
  selector: 'app-purchase',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './purchase.component.html',
  styleUrls: ['./purchase.component.css']
})
export class PurchaseComponent implements OnInit {
  purchaseData: any;
  purchase: PolicyPurchase | null = null;
  isLoading = false;
  errorMessage = '';
  successMessage = '';
  userId: number | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private purchaseService: PurchaseService,
    private vehicleService: VehicleService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.userId = this.authService.getCurrentUser()?.id || null;
    this.route.queryParams.subscribe(params => {
      if (params['data']) {
        this.purchaseData = JSON.parse(params['data']);
      }
    });
  }

  confirmPurchase(): void {
    if (!this.userId || !this.purchaseData?.entityId || !this.purchaseData?.policyType) {
      this.errorMessage = 'Missing purchase data.';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    const today = new Date();
    const expiry = new Date(today);
    expiry.setFullYear(today.getFullYear() + 1);

    const request: any = {
      userId: this.userId,
      purchaseDate: today.toISOString().substring(0, 10),
      expiryDate: expiry.toISOString().substring(0, 10)
    };

    const onSuccess = () => {
      this.successMessage = 'Purchase successful!';
      this.isLoading = false;
      setTimeout(() => this.router.navigate(['/customer/dashboard/my-purchases']), 600);
    };

    const onError = () => {
      this.errorMessage = 'Failed to complete purchase.';
      this.isLoading = false;
    };

    const type = (this.purchaseData.policyType as string).toLowerCase();
    if (type === 'bike') {
      this.vehicleService.confirmBikePurchase(this.purchaseData.entityId, request).subscribe({ next: onSuccess, error: onError });
    } else if (type === 'car') {
      this.vehicleService.confirmCarPurchase(this.purchaseData.entityId, request).subscribe({ next: onSuccess, error: onError });
    } else if (type === 'health') {
      this.vehicleService.confirmHealthPurchase(this.purchaseData.entityId, request).subscribe({ next: onSuccess, error: onError });
    } else if (type === 'life') {
      this.vehicleService.confirmLifePurchase(this.purchaseData.entityId, request).subscribe({ next: onSuccess, error: onError });
    } else {
      this.isLoading = false;
      this.errorMessage = 'Unsupported policy type.';
    }
  }

  cancelPurchase(): void {
    if (!this.purchase) return;
    this.isLoading = true;
    this.purchaseService.cancelPurchase(this.purchase.purchaseId).subscribe({
      next: () => {
        this.successMessage = 'Purchase cancelled.';
        this.purchase = null;
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to cancel purchase.';
        this.isLoading = false;
      }
    });
  }

  goToMyPurchases(): void {
    this.router.navigate(['/customer/dashboard/my-purchases']);
  }

  goBack(): void {
    this.router.navigate(['/customer/dashboard/policy-view']);
  }
}
