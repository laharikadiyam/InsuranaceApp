import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { AuthService } from './auth.service';

export interface LifeInsuranceDTO {
  id?: number;
  age: number;
  gender: string;
  sumAssured: number;
  policyTerm: number;
  smoker: boolean;
  occupationRisk: string;
  premium?: number;
  user_id: number;
  status?: string;
}

export interface LifeInsurance {
  id: number;
  age: number;
  gender: string;
  sumAssured: number;
  policyTerm: number;
  smoker: boolean;
  occupationRisk: string;
  premium: number;
  status: string;
  user: any;
  purchase?: any;
}

@Injectable({
  providedIn: 'root'
})
export class LifeInsuranceService {
  private apiUrl = `${environment.apiUrl}/api/customer/life`;

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  addLife(dto: LifeInsuranceDTO): Observable<LifeInsuranceDTO> {
    return this.http.post<LifeInsuranceDTO>(`${this.apiUrl}/addLife`, dto, {
      headers: this.authService.getAuthHeaders()
    });
  }

  confirmPurchase(lifeId: number, request: any): Observable<LifeInsuranceDTO> {
    return this.http.post<LifeInsuranceDTO>(`${this.apiUrl}/confirm/${lifeId}`, request, {
      headers: this.authService.getAuthHeaders()
    });
  }

  cancelPendingLife(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/cancel/${id}`, {
      headers: this.authService.getAuthHeaders()
    });
  }

  updateLife(id: number, dto: LifeInsuranceDTO): Observable<LifeInsuranceDTO> {
    return this.http.put<LifeInsuranceDTO>(`${this.apiUrl}/updateLife/${id}`, dto, {
      headers: this.authService.getAuthHeaders()
    });
  }

  getAll(): Observable<LifeInsuranceDTO[]> {
    return this.http.get<LifeInsuranceDTO[]>(`${this.apiUrl}/get`, {
      headers: this.authService.getAuthHeaders()
    });
  }

  deleteLife(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/delete/${id}`, {
      headers: this.authService.getAuthHeaders()
    });
  }

  calculatePremium(dto: LifeInsuranceDTO): Observable<number> {
    return this.http.post<number>(`${this.apiUrl}/calculate-premium`, dto, {
      headers: this.authService.getAuthHeaders()
    });
  }
}
