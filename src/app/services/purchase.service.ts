import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { AuthService } from './auth.service';
import { PolicyPurchaseRequest, PolicyPurchase } from '../models/purchase.model';

@Injectable({ providedIn: 'root' })
export class PurchaseService {
  private apiUrl = environment.apiUrl + '/api/customer/purchases';

  constructor(private http: HttpClient, private authService: AuthService) {}

  createPurchase(request: PolicyPurchaseRequest): Observable<PolicyPurchase> {
    return this.http.post<PolicyPurchase>(this.apiUrl, request, {
      headers: this.authService.getAuthHeaders()
    });
  }

  getPurchasesByUser(userId: number, activeOnly?: boolean): Observable<PolicyPurchase[]> {
    let params: any = { userId };
    if (activeOnly !== undefined && activeOnly !== null) {
      params.activeOnly = activeOnly;
    }
    return this.http.get<PolicyPurchase[]>(this.apiUrl, {
      headers: this.authService.getAuthHeaders(),
      params
    });
  }

  getPurchaseById(id: number): Observable<PolicyPurchase> {
    return this.http.get<PolicyPurchase>(`${this.apiUrl}/${id}`, {
      headers: this.authService.getAuthHeaders()
    });
  }

  updatePurchase(id: number, request: PolicyPurchaseRequest): Observable<PolicyPurchase> {
    return this.http.put<PolicyPurchase>(`${this.apiUrl}/${id}`, request, {
      headers: this.authService.getAuthHeaders()
    });
  }

  cancelPurchase(id: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/cancel/${id}`, {}, {
      headers: this.authService.getAuthHeaders()
    });
  }
}






