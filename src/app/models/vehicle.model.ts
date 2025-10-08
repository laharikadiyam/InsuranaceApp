// Vehicle Models
export interface BikeDTO {
  vehicle_id?: number;
  cc: number;
  ageinMonths: number;
  idv: number;
  manufacturerName: string;
  registrationNumber: string;
  thirdPartyPremium: number;
  comprehensivePremium: number;
  userid?: number;
  purchaseId?: number;
  status?: string; // PENDING, CONFIRMED, CANCELLED
}

export interface CarDTO {
  id?: number;
  cc: number;
  ageinMonths: number;
  idv: number;
  manufacturerName: string;
  registrationNumber: string;
  thirdPartyPremium: number;
  comprehensivePremium: number;
  userid?: number; // added to link user
  status?: string; // PENDING, CONFIRMED, CANCELLED
}

// Insurance Models
export interface HealthInsuranceDTO {
  age: number;
  numberOfMembers: number;
  sumInsured: number;
  smoker: boolean;
  preExisting: boolean;
  user_id?: number;
  status?: string; // PENDING, CONFIRMED, CANCELLED
}

export interface LifeInsuranceDTO {
  age: number;
  gender: string;
  sumAssured: number;
  policyTerm: number;
  smoker: boolean;
  occupationRisk: string;
  user_id?: number; // added to link user
  status?: string; // PENDING, CONFIRMED, CANCELLED
}

export interface InsurancePremiumResponse {
  annualPremium: number;
  monthlyPremium: number;
}

// Premium Calculation Response
export interface PremiumCalculationResponse {
  idv?: number;
  thirdPartyPremium?: number;
  comprehensivePremium?: number;
  annualPremium?: number;
  monthlyPremium?: number;
  [key: string]: number | undefined;
}

// Policy Purchase Request
export interface PolicyPurchaseRequest {
  policyId: number;
  customerId: number;
  premiumAmount: number;
  startDate: string;
  endDate: string;
}
