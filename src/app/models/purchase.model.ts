export interface PolicyPurchaseRequest {
  userId: number;
  bikePolicyId?: number;
  carPolicyId?: number;
  healthPolicyId?: number;
  lifePolicyId?: number;
  purchaseDate: string; // ISO string
  expiryDate: string; // ISO string
}

export interface PolicyPurchase {
  purchaseId: number;
  purchaseDate: string;
  expiryDate: string;
  status?: string; // ACTIVE, CANCELLED, EXPIRED
  user: any;
  bikePolicy?: any;
  carPolicy?: any;
  healthPolicy?: any;
  lifePolicy?: any;
}






