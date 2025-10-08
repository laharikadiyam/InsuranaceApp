import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Claim, ClaimDTO, ClaimStatusUpdateDTO } from '../models/claim.model';
import { AuthService } from './auth.service';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ClaimService {
  private baseUrl = environment.apiUrl;

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  // Admin endpoints
  getAllClaims(): Observable<Claim[]> {
    return this.http.get<Claim[]>(`${this.baseUrl}/api/admin/claims`, {
      headers: this.authService.getAuthHeaders()
    });
  }

  updateClaimStatus(claimId: number, status: string): Observable<Claim> {
    const statusUpdate: ClaimStatusUpdateDTO = { status };
    return this.http.put<Claim>(`${this.baseUrl}/api/admin/claims/${claimId}/status`, statusUpdate, {
      headers: this.authService.getAuthHeaders()
    });
  }

  // Customer endpoints
  raiseClaim(claim: ClaimDTO): Observable<Claim> {
    return this.http.post<Claim>(`${this.baseUrl}/api/customer/claims`, claim, {
      headers: this.authService.getAuthHeaders()
    });
  }

  getClaimsByUser(userId: number): Observable<Claim[]> {
    return this.http.get<Claim[]>(`${this.baseUrl}/api/customer/claims/user/${userId}`, {
      headers: this.authService.getAuthHeaders()
    });
  }

  getClaimById(claimId: number): Observable<Claim> {
    return this.http.get<Claim>(`${this.baseUrl}/api/customer/claims/${claimId}`, {
      headers: this.authService.getAuthHeaders()
    });
  }

  deleteClaim(claimId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/api/customer/claims/${claimId}`, {
      headers: this.authService.getAuthHeaders()
    });
  }
}



