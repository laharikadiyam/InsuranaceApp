import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Policy, PolicyDto, CreatePolicyRequest, UpdatePolicyRequest } from '../models/policy.model';
import { environment } from '../../environments/environment';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class PolicyService {
  private adminApiUrl = `${environment.apiUrl}/api/admin/policies`;
  private customerApiUrl = `${environment.apiUrl}/api/customer/availablepolicies`;

  constructor(private http: HttpClient, private authService: AuthService) { }

  // Admin endpoints
  createPolicy(policy: CreatePolicyRequest): Observable<PolicyDto> {
    return this.http.post<PolicyDto>(this.adminApiUrl, policy, {
      headers: this.authService.getAuthHeaders()
    });
  }

  updatePolicy(id: number, policy: UpdatePolicyRequest): Observable<PolicyDto> {
    return this.http.put<PolicyDto>(`${this.adminApiUrl}/${id}`, policy, {
      headers: this.authService.getAuthHeaders()
    });
  }

  getPolicyById(id: number): Observable<PolicyDto> {
    return this.http.get<PolicyDto>(`${this.adminApiUrl}/${id}`, {
      headers: this.authService.getAuthHeaders()
    });
  }

  getAllPolicies(type?: string, activeOnly?: boolean): Observable<PolicyDto[]> {
    let params = new HttpParams();
    if (type) {
      params = params.set('type', type);
    }
    if (activeOnly !== undefined && activeOnly !== null) {
      params = params.set('activeOnly', activeOnly.toString());
    }
    return this.http.get<PolicyDto[]>(this.adminApiUrl, { 
      params,
      headers: this.authService.getAuthHeaders()
    });
  }

  deletePolicy(id: number): Observable<void> {
    return this.http.delete<void>(`${this.adminApiUrl}/${id}`, {
      headers: this.authService.getAuthHeaders()
    });
  }

  // Customer endpoints
  getAvailablePolicies(type?: string, activeOnly?: boolean): Observable<PolicyDto[]> {
    let params = new HttpParams();
    if (type) {
      params = params.set('type', type);
    }
    if (activeOnly !== undefined && activeOnly !== null) {
      params = params.set('activeOnly', activeOnly.toString());
    }
    return this.http.get<PolicyDto[]>(this.customerApiUrl, { 
      params,
      headers: this.authService.getAuthHeaders()
    });
  }
}
