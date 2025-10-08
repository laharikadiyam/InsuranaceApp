import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { PolicyService } from '../../../services/policy.service';
import { PolicyDto, CreatePolicyRequest, UpdatePolicyRequest } from '../../../models/policy.model';

@Component({
  selector: 'app-policy-management',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule],
  templateUrl: './policy-management.component.html',
  styleUrls: ['./policy-management.component.css']
})
export class PolicyManagementComponent implements OnInit {
  policies: PolicyDto[] = [];
  isLoading = false;
  errorMessage = '';
  successMessage = '';
  
  // Form states
  showAddForm = false;
  showEditForm = false;
  editingPolicy: PolicyDto | null = null;
  
  // Filter states
  filterType = '';
  filterActiveOnly: boolean | null = null;
  
  // Forms
  policyForm: FormGroup;

  constructor(
    private policyService: PolicyService,
    private fb: FormBuilder
  ) {
    this.policyForm = this.fb.group({
      policyName: ['', [Validators.required, Validators.maxLength(100)]],
      type: ['', [Validators.required, Validators.maxLength(50)]],
      premium: [0, [Validators.required, Validators.min(0)]],
      tenure: [6, [Validators.required, Validators.min(1), Validators.max(600)]],
      coverage: ['', [Validators.required, Validators.maxLength(255)]],
      active: [true]
    });
  }

  ngOnInit(): void {
    this.loadPolicies();
  }

  loadPolicies(): void {
    this.isLoading = true;
    this.errorMessage = '';
    
    this.policyService.getAllPolicies(this.filterType || undefined, this.filterActiveOnly || undefined)
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

  showAddPolicyForm(): void {
    this.showAddForm = true;
    this.showEditForm = false;
    this.editingPolicy = null;
    this.policyForm.reset({
      policyName: '',
      type: '',
      premium: 0,
      tenure: 6,
      coverage: '',
      active: true
    });
  }

  showEditPolicyForm(policy: PolicyDto): void {
    this.showEditForm = true;
    this.showAddForm = false;
    this.editingPolicy = policy;
    this.policyForm.patchValue({
      policyName: policy.policyName,
      type: policy.type,
      premium: policy.premium,
      tenure: policy.tenure,
      coverage: policy.coverage,
      active: policy.active
    });
  }

  cancelForm(): void {
    this.showAddForm = false;
    this.showEditForm = false;
    this.editingPolicy = null;
    this.policyForm.reset();
  }

  onSubmit(): void {
    if (this.policyForm.valid) {
      const formValue = this.policyForm.value;
      
      if (this.showAddForm) {
        this.createPolicy(formValue);
      } else if (this.showEditForm && this.editingPolicy) {
        this.updatePolicy(this.editingPolicy.policy_id!, formValue);
      }
    }
  }

  createPolicy(policyData: CreatePolicyRequest): void {
    this.isLoading = true;
    this.errorMessage = '';
    
    this.policyService.createPolicy(policyData).subscribe({
      next: (createdPolicy) => {
        this.policies.push(createdPolicy);
        this.successMessage = 'Policy created successfully!';
        this.cancelForm();
        this.isLoading = false;
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (error) => {
        this.errorMessage = 'Failed to create policy. Please try again.';
        this.isLoading = false;
        console.error('Error creating policy:', error);
      }
    });
  }

  updatePolicy(id: number, policyData: UpdatePolicyRequest): void {
    this.isLoading = true;
    this.errorMessage = '';
    
    this.policyService.updatePolicy(id, policyData).subscribe({
      next: (updatedPolicy) => {
        const index = this.policies.findIndex(p => p.policy_id === id);
        if (index !== -1) {
          this.policies[index] = updatedPolicy;
        }
        this.successMessage = 'Policy updated successfully!';
        this.cancelForm();
        this.isLoading = false;
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (error) => {
        this.errorMessage = 'Failed to update policy. Please try again.';
        this.isLoading = false;
        console.error('Error updating policy:', error);
      }
    });
  }

  deletePolicy(id: number): void {
    if (confirm('Are you sure you want to delete this policy? This action cannot be undone.')) {
      this.isLoading = true;
      this.errorMessage = '';
      
      this.policyService.deletePolicy(id).subscribe({
        next: () => {
          this.policies = this.policies.filter(p => p.policy_id !== id);
          this.successMessage = 'Policy deleted successfully!';
          this.isLoading = false;
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (error) => {
          this.errorMessage = 'Failed to delete policy. Please try again.';
          this.isLoading = false;
          console.error('Error deleting policy:', error);
        }
      });
    }
  }

  applyFilters(): void {
    this.loadPolicies();
  }

  clearFilters(): void {
    this.filterType = '';
    this.filterActiveOnly = null;
    this.loadPolicies();
  }

  getStatusBadgeClass(active: boolean): string {
    return active ? 'badge bg-success' : 'badge bg-danger';
  }

  getStatusText(active: boolean): string {
    return active ? 'Active' : 'Inactive';
  }
}
