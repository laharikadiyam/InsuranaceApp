export interface Claim {
  claimId?: number;
  id?: number; // Alternative field name from backend
  userId?: number;
  purchaseId?: number;
  claimStatus: string;
  uploadedAt: string;
  user?: {
    id: number;
    name?: string;
    email?: string;
  };
  purchase?: {
    purchaseId: number;
    purchaseDate?: string;
    expiryDate?: string;
  };
}

export interface ClaimDTO {
  claimId?: number;
  userId: number;
  purchaseId: number;
  claimStatus: string;
  uploadedAt?: string;
}

export interface ClaimStatusUpdateDTO {
  status: string;
}
