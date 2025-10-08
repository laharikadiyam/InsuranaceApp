export enum Roles {
  ADMIN = 'ADMIN',
  CUSTOMER = 'CUSTOMER'
}

export interface User {
  id: number;
  name: string;
  email: string;
  role: Roles;
  isActive: boolean;
  panNumber: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  id: number;
  name: string;
  email: string;
  role: Roles;
  token: string;
}

export interface RegistrationRequest {
  name: string;
  email: string;
  password: string;
  role: Roles;
  panNumber: string;
}

export interface ChangePasswordRequest {
  email: string;
  oldPassword: string;
  newPassword: string;
}

export interface ForgotPasswordRequest {
  email: string;
  panNumber: string;
  newPassword: string;
}

export interface UserProfileResponse {
  id: number;
  name: string;
  email: string;
  role: Roles;
  isActive: boolean;
  panNumber: string;
}
