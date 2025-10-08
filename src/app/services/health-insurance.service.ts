import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { AuthService } from './auth.service';

export interface HealthInsuranceDTO {
  id?: number;
  age: number;
  numberOfMembers: number;
  sumInsured: number;
  smoker: boolean;
  preExisting: boolean;
  premium?: number;
  user_id: number;
  status?: string;
}

export interface HealthInsurance {
  id: number;
  age: number;
  numberOfMembers: number;
  sumInsured: number;
  smoker: boolean;
  preExisting: boolean;
  premium: number;
  status: string;
  user: any;
  purchase?: any;
}

@Injectable({
  providedIn: 'root'
})
export class HealthInsuranceService {
  private apiUrl = `${environment.apiUrl}/api/customer/health`;

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  addHealth(dto: HealthInsuranceDTO): Observable<HealthInsuranceDTO> {
    return this.http.post<HealthInsuranceDTO>(`${this.apiUrl}/addHealth`, dto, {
      headers: this.authService.getAuthHeaders()
    });
  }

  confirmPurchase(healthId: number, request: any): Observable<HealthInsuranceDTO> {
    return this.http.post<HealthInsuranceDTO>(`${this.apiUrl}/confirm/${healthId}`, request, {
      headers: this.authService.getAuthHeaders()
    });
  }

  cancelPendingHealth(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/cancel/${id}`, {
      headers: this.authService.getAuthHeaders()
    });
  }

  updateHealth(id: number, dto: HealthInsuranceDTO): Observable<HealthInsuranceDTO> {
    return this.http.put<HealthInsuranceDTO>(`${this.apiUrl}/update/${id}`, dto, {
      headers: this.authService.getAuthHeaders()
    });
  }

  getAll(): Observable<HealthInsuranceDTO[]> {
    return this.http.get<HealthInsuranceDTO[]>(`${this.apiUrl}/get`, {
      headers: this.authService.getAuthHeaders()
    });
  }

  deleteHealth(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/delete/${id}`, {
      headers: this.authService.getAuthHeaders()
    });
  }

  calculatePremium(dto: HealthInsuranceDTO): Observable<number> {
    return this.http.post<number>(`${this.apiUrl}/calculate-premium`, dto, {
      headers: this.authService.getAuthHeaders()
    });
  }
}
