import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { VehicleService } from '../../../services/vehicle.service';
import { HealthInsuranceService, HealthInsuranceDTO as HealthDTO } from '../../../services/health-insurance.service';
import { LifeInsuranceService, LifeInsuranceDTO as LifeDTO } from '../../../services/life-insurance.service';
import { AuthService } from '../../../services/auth.service';
import { 
  BikeDTO, 
  CarDTO, 
  PremiumCalculationResponse
} from '../../../models/vehicle.model';
import { PolicyDto } from '../../../models/policy.model';

@Component({
  selector: 'app-vehicle-management',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule],
  templateUrl: './vehicle-management.component.html',
  styleUrls: ['./vehicle-management.component.css']
})
export class VehicleManagementComponent implements OnInit {
  policyType: string = '';
  policy: PolicyDto | null = null;
  isLoading = false;
  errorMessage = '';
  successMessage = '';
  calculatedPremium: PremiumCalculationResponse | null = null;

  createdEntityId: number | null = null;
  createdEntityType: 'bike' | 'car' | 'health' | 'life' | '' = '';
  
  // Form states
  showBikeForm = false;
  showCarForm = false;
  showHealthForm = false;
  showLifeForm = false;
  
  // Forms
  bikeForm: FormGroup;
  carForm: FormGroup;
  healthForm: FormGroup;
  lifeForm: FormGroup;

  constructor(
    private vehicleService: VehicleService,
    private healthService: HealthInsuranceService,
    private lifeService: LifeInsuranceService,
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router,
    private fb: FormBuilder
  ) {
    this.bikeForm = this.fb.group({
      cc: [0, [Validators.required, Validators.min(1)]],
      ageinMonths: [0, [Validators.required, Validators.min(0)]],
      manufacturerName: ['', [Validators.required]],
      registrationNumber: ['', [Validators.required]]
    });

    this.carForm = this.fb.group({
      cc: [0, [Validators.required, Validators.min(1)]],
      ageinMonths: [0, [Validators.required, Validators.min(0)]],
      manufacturerName: ['', [Validators.required]],
      registrationNumber: ['', [Validators.required]]
    });

    this.healthForm = this.fb.group({
      age: [0, [Validators.required, Validators.min(1), Validators.max(100)]],
      numberOfMembers: [1, [Validators.required, Validators.min(1)]],
      sumInsured: [0, [Validators.required, Validators.min(10000)]],
      smoker: [false],
      preExisting: [false]
    });

    this.lifeForm = this.fb.group({
      age: [0, [Validators.required, Validators.min(18), Validators.max(70)]],
      gender: ['', [Validators.required]],
      sumAssured: [0, [Validators.required, Validators.min(100000)]],
      policyTerm: [0, [Validators.required, Validators.min(1)]],
      smoker: [false],
      occupationRisk: ['low', [Validators.required]]
    });
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.policyType = (params['type'] || '').toLowerCase();
      this.policy = params['policy'] ? JSON.parse(params['policy']) : null;
      this.initializeForm();
    });
  }

  initializeForm(): void {
    this.showBikeForm = this.policyType === 'bike';
    this.showCarForm = this.policyType === 'car';
    this.showHealthForm = this.policyType === 'health';
    this.showLifeForm = this.policyType === 'life';
    if (!this.showBikeForm && !this.showCarForm && !this.showHealthForm && !this.showLifeForm) {
      this.errorMessage = 'Invalid policy type';
    }
  }

  calculatePremium(): void {
    this.isLoading = true;
    this.errorMessage = '';

    switch (this.policyType) {
      case 'bike':
        this.calculateBikeAndSave();
        break;
      case 'car':
        this.calculateCarAndSave();
        break;
      case 'health':
        this.calculateHealthAndSave();
        break;
      case 'life':
        this.calculateLifeAndSave();
        break;
      default:
        this.isLoading = false;
        this.errorMessage = 'Invalid policy type';
    }
  }

  private calculateBikeAndSave(): void {
    if (!this.bikeForm.valid) { this.isLoading = false; return; }
    const req: BikeDTO = {
      ...this.bikeForm.value,
      idv: 0,
      thirdPartyPremium: 0,
      comprehensivePremium: 0,
      userid: this.authService.getCurrentUser()?.id
    };
    this.vehicleService.calculateBikePremium(req).subscribe({
      next: (calc) => {
        this.calculatedPremium = calc;
        const payload: BikeDTO = {
          ...this.bikeForm.value,
          idv: calc.idv || 0,
          thirdPartyPremium: calc.thirdPartyPremium || 0,
          comprehensivePremium: calc.comprehensivePremium || 0,
          userid: this.authService.getCurrentUser()?.id
        };
        if (!this.createdEntityId) {
          this.vehicleService.addBike(payload).subscribe({
            next: (saved) => {
              this.createdEntityId = saved.vehicle_id || null;
              this.createdEntityType = 'bike';
              this.isLoading = false;
              this.successMessage = 'Bike details saved (Pending).';
              setTimeout(() => this.successMessage = '', 2500);
            },
            error: () => { this.isLoading = false; this.errorMessage = 'Failed to save bike.'; }
          });
        } else {
          this.vehicleService.updateBike(this.createdEntityId, payload).subscribe({
            next: () => { this.isLoading = false; this.successMessage = 'Bike details updated.'; setTimeout(() => this.successMessage = '', 2000); },
            error: () => { this.isLoading = false; this.errorMessage = 'Failed to update bike.'; }
          });
        }
      },
      error: () => { this.isLoading = false; this.errorMessage = 'Failed to calculate premium.'; }
    });
  }

  private calculateCarAndSave(): void {
    if (!this.carForm.valid) { this.isLoading = false; return; }
    const req: CarDTO = {
      ...this.carForm.value,
      idv: 0,
      thirdPartyPremium: 0,
      comprehensivePremium: 0,
      userid: this.authService.getCurrentUser()?.id
    } as any;
    this.vehicleService.calculateCarPremium(req).subscribe({
      next: (calc) => {
        this.calculatedPremium = calc;
        const payload: CarDTO = {
          ...this.carForm.value,
          idv: calc.idv || 0,
          thirdPartyPremium: calc.thirdPartyPremium || 0,
          comprehensivePremium: calc.comprehensivePremium || 0,
          userid: this.authService.getCurrentUser()?.id
        } as any;
        if (!this.createdEntityId) {
          this.vehicleService.addCar(payload).subscribe({
            next: (saved) => {
              this.createdEntityId = (saved as any).id || null;
              this.createdEntityType = 'car';
              this.isLoading = false;
              this.successMessage = 'Car details saved (Pending).';
              setTimeout(() => this.successMessage = '', 2500);
            },
            error: () => { this.isLoading = false; this.errorMessage = 'Failed to save car.'; }
          });
        } else {
          this.vehicleService.updateCar(this.createdEntityId, payload).subscribe({
            next: () => { this.isLoading = false; this.successMessage = 'Car details updated.'; setTimeout(() => this.successMessage = '', 2000); },
            error: () => { this.isLoading = false; this.errorMessage = 'Failed to update car.'; }
          });
        }
      },
      error: () => { this.isLoading = false; this.errorMessage = 'Failed to calculate premium.'; }
    });
  }

  private calculateHealthAndSave(): void {
    if (!this.healthForm.valid) { this.isLoading = false; return; }
    const currentUser = this.authService.getCurrentUser();
    if (!currentUser?.id) {
      this.isLoading = false;
      this.errorMessage = 'User not authenticated';
      return;
    }
    
    const payload: HealthDTO = {
      ...this.healthForm.value,
      user_id: currentUser.id
    };
    
    // First calculate premium
    this.healthService.calculatePremium(payload).subscribe({
      next: (premium) => {
        this.calculatedPremium = { annualPremium: premium };
        
        // Then save the entity
        if (!this.createdEntityId) {
          this.healthService.addHealth(payload).subscribe({
            next: (saved: any) => {
              this.createdEntityId = saved.id || null;
              this.createdEntityType = 'health';
              this.isLoading = false;
              this.successMessage = 'Health insurance saved (Pending).';
              setTimeout(() => this.successMessage = '', 2500);
            },
            error: () => { this.isLoading = false; this.errorMessage = 'Failed to save health insurance.'; }
          });
        } else {
          this.healthService.updateHealth(this.createdEntityId, payload).subscribe({
            next: () => { this.isLoading = false; this.successMessage = 'Health insurance updated.'; setTimeout(() => this.successMessage = '', 2000); },
            error: () => { this.isLoading = false; this.errorMessage = 'Failed to update health insurance.'; }
          });
        }
      },
      error: () => { this.isLoading = false; this.errorMessage = 'Failed to calculate premium.'; }
    });
  }

  private calculateLifeAndSave(): void {
    if (!this.lifeForm.valid) { this.isLoading = false; return; }
    const currentUser = this.authService.getCurrentUser();
    if (!currentUser?.id) {
      this.isLoading = false;
      this.errorMessage = 'User not authenticated';
      return;
    }
    
    const payload: LifeDTO = {
      ...this.lifeForm.value,
      user_id: currentUser.id
    };
    
    // First calculate premium
    this.lifeService.calculatePremium(payload).subscribe({
      next: (premium) => {
        this.calculatedPremium = { annualPremium: premium };
        
        // Then save the entity
        if (!this.createdEntityId) {
          this.lifeService.addLife(payload).subscribe({
            next: (saved: any) => {
              this.createdEntityId = saved.id || null;
              this.createdEntityType = 'life';
              this.isLoading = false;
              this.successMessage = 'Life insurance saved (Pending).';
              setTimeout(() => this.successMessage = '', 2500);
            },
            error: () => { this.isLoading = false; this.errorMessage = 'Failed to save life insurance.'; }
          });
        } else {
          this.lifeService.updateLife(this.createdEntityId, payload).subscribe({
            next: () => { this.isLoading = false; this.successMessage = 'Life insurance updated.'; setTimeout(() => this.successMessage = '', 2000); },
            error: () => { this.isLoading = false; this.errorMessage = 'Failed to update life insurance.'; }
          });
        }
      },
      error: () => { this.isLoading = false; this.errorMessage = 'Failed to calculate premium.'; }
    });
  }

  cancelPending(): void {
    if (!this.createdEntityId || !this.createdEntityType) return;
    this.isLoading = true;
    const id = this.createdEntityId;
    const type = this.createdEntityType;
    const onDone = () => {
      this.createdEntityId = null;
      this.createdEntityType = '';
      this.calculatedPremium = null;
      this.isLoading = false;
      this.successMessage = 'Pending details cancelled.';
      setTimeout(() => this.successMessage = '', 2000);
    };
    const onError = () => {
      this.isLoading = false;
      this.errorMessage = 'Failed to cancel pending details.';
    };
    if (type === 'bike') {
      this.vehicleService.cancelBike(id).subscribe({ next: onDone, error: onError });
    } else if (type === 'car') {
      this.vehicleService.cancelCar(id).subscribe({ next: onDone, error: onError });
    } else if (type === 'health') {
      this.healthService.cancelPendingHealth(id).subscribe({ next: onDone, error: onError });
    } else if (type === 'life') {
      this.lifeService.cancelPendingLife(id).subscribe({ next: onDone, error: onError });
    }
  }

  confirmPurchase(): void {
    if (!this.createdEntityId || !this.createdEntityType) {
      this.errorMessage = 'Nothing to confirm. Please calculate and save first.';
      return;
    }
    this.isLoading = true;
    this.errorMessage = '';

    const today = new Date();
    const expiry = new Date(today);
    expiry.setFullYear(today.getFullYear() + 1);
    const request: any = {
      userId: this.authService.getCurrentUser()?.id,
      purchaseDate: today.toISOString().substring(0, 10),
      expiryDate: expiry.toISOString().substring(0, 10)
    };

    const id = this.createdEntityId;
    const onSuccess = () => {
      this.isLoading = false;
      this.successMessage = 'Purchase confirmed!';
      setTimeout(() => this.router.navigate(['/customer/dashboard/my-purchases']), 800);
    };
    const onError = () => {
      this.isLoading = false;
      this.errorMessage = 'Failed to complete purchase.';
    };

    if (this.createdEntityType === 'bike') {
      this.vehicleService.confirmBikePurchase(id, request).subscribe({ next: onSuccess, error: onError });
    } else if (this.createdEntityType === 'car') {
      this.vehicleService.confirmCarPurchase(id, request).subscribe({ next: onSuccess, error: onError });
    } else if (this.createdEntityType === 'health') {
      this.healthService.confirmPurchase(id, request).subscribe({ next: onSuccess, error: onError });
    } else if (this.createdEntityType === 'life') {
      this.lifeService.confirmPurchase(id, request).subscribe({ next: onSuccess, error: onError });
    }
  }

  goBack(): void {
    this.router.navigate(['/customer/dashboard/policy-view']);
  }
}
