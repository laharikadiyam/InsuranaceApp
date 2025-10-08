import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { PolicyService } from '../../../services/policy.service';
import { PolicyDto } from '../../../models/policy.model';

@Component({
  selector: 'app-policy-view',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './policy-view.component.html',
  styleUrls: ['./policy-view.component.css']
})
export class PolicyViewComponent implements OnInit {
  policies: PolicyDto[] = [];
  isLoading = false;
  errorMessage = '';
  
  // Filter states
  filterType = '';
  filterActiveOnly: boolean | null = true; // Default to active policies only

  constructor(
    private policyService: PolicyService, 
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loadPolicies();
  }

  loadPolicies(): void {
    this.isLoading = true;
    this.errorMessage = '';
    
    // Load admin-created policies that are available to customers
    this.policyService.getAvailablePolicies(this.filterType || undefined, this.filterActiveOnly || undefined)
      .subscribe({
        next: (policies) => {
          this.policies = policies;
          this.isLoading = false;
        },
        error: (error) => {
          this.errorMessage = 'Failed to load policies. Please try again.';
          this.isLoading = false;
          console.error('Error loading policies:', error);
        }
      });
  }

  applyFilters(): void {
    this.loadPolicies();
  }

  clearFilters(): void {
    this.filterType = '';
    this.filterActiveOnly = true;
    this.loadPolicies();
  }

  getStatusBadgeClass(active: boolean): string {
    return active ? 'badge bg-success' : 'badge bg-danger';
  }

  getStatusText(active: boolean): string {
    return active ? 'Active' : 'Inactive';
  }

  viewDetails(policy: PolicyDto): void {
    // Navigate to vehicle-management with the selected policy
    this.router.navigate(['/customer/dashboard/vehicle-management'], {
      queryParams: {
        type: policy.type.toLowerCase(),
        policy: JSON.stringify(policy)
      }
    });
  }
}
