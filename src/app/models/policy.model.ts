export interface Policy {
  policy_id?: number;
  policyName: string;
  type: string;
  premium: number;
  tenure: number;
  coverage: string;
  active: boolean;
}

export interface PolicyDto {
  policy_id?: number;
  policyName: string;
  type: string;
  premium: number;
  tenure: number;
  coverage: string;
  active: boolean;
}

export interface CreatePolicyRequest {
  policyName: string;
  type: string;
  premium: number;
  tenure: number;
  coverage: string;
  active: boolean;
}

export interface UpdatePolicyRequest {
  policyName: string;
  type: string;
  premium: number;
  tenure: number;
  coverage: string;
  active: boolean;
}




