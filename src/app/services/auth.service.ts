import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable, tap, catchError, map } from 'rxjs';
import { environment } from '../../environments/environment';
import { 
  LoginRequest, 
  LoginResponse, 
  RegistrationRequest, 
  ChangePasswordRequest, 
  ForgotPasswordRequest, 
  UserProfileResponse,
  Roles 
} from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = environment.apiUrl;
  private currentUserSubject = new BehaviorSubject<LoginResponse | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();
  


  constructor(private http: HttpClient) {
    this.loadStoredUser();
  }

  private loadStoredUser(): void {
    const storedUser = localStorage.getItem('currentUser');
    if (storedUser) {
      this.currentUserSubject.next(JSON.parse(storedUser));
    }
  }

  register(request: RegistrationRequest): Observable<string> {
    return this.http.post<string>(`${this.apiUrl}/api/auth/register`, request)
      .pipe(
        tap(response => console.log('Registration successful:', response)),
        catchError(error => {
          console.error('Registration error:', error);
          throw error;
        })
      );
  }

  loginCustomer(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/api/auth/login/customer`, request)
      .pipe(
        tap(response => {
          console.log('Customer login successful:', response);
          this.setCurrentUser(response);
        }),
        catchError(error => {
          console.error('Customer login error:', error);
          throw error;
        })
      );
  }

  loginAdmin(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/api/auth/login/admin`, request)
      .pipe(
        tap(response => {
          console.log('Admin login successful:', response);
          this.setCurrentUser(response);
        }),
        catchError(error => {
          console.error('Admin login error:', error);
          throw error;
        })
      );
  }

  forgotPassword(request: ForgotPasswordRequest): Observable<string> {
    return this.http.post<string>(`${this.apiUrl}/api/auth/forgot-password`, request)
      .pipe(
        tap(response => console.log('Forgot password successful:', response)),
        catchError(error => {
          console.error('Forgot password error:', error);
          throw error;
        })
      );
  }

  changePassword(request: ChangePasswordRequest): Observable<string> {
    return this.http.post<string>(`${this.apiUrl}/api/auth/change-password`, request, {
      headers: this.getAuthHeaders()
    });
  }

  getCustomerProfile(): Observable<UserProfileResponse> {
    return this.http.get<UserProfileResponse>(`${this.apiUrl}/api/customer/profile`, {
      headers: this.getAuthHeaders()
    }).pipe(
      map((response: any) => this.normalizeUserProfile(response)),
      tap(response => console.log('Customer profile loaded:', response)),
      catchError(error => {
        console.error('Error loading customer profile:', error);
        throw error;
      })
    );
  }

  getAdminProfile(): Observable<UserProfileResponse> {
    return this.http.get<UserProfileResponse>(`${this.apiUrl}/api/admin/profile`, {
      headers: this.getAuthHeaders()
    }).pipe(
      map((response: any) => this.normalizeUserProfile(response)),
      tap(response => console.log('Admin profile loaded:', response)),
      catchError(error => {
        console.error('Error loading admin profile:', error);
        throw error;
      })
    );
  }

  getPendingAdmins(): Observable<UserProfileResponse[]> {
    return this.http.get<UserProfileResponse[]>(`${this.apiUrl}/api/admin/pending-admins`, {
      headers: this.getAuthHeaders()
    }).pipe(
      map((response: any[]) => response?.map(u => this.normalizeUserProfile(u)) ?? []),
      tap(response => console.log('Pending admins loaded:', response)),
      catchError(error => {
        console.error('Error loading pending admins:', error);
        throw error;
      })
    );
  }

  getPendingCustomers(): Observable<UserProfileResponse[]> {
    return this.http.get<UserProfileResponse[]>(`${this.apiUrl}/api/admin/pending-customers`, {
      headers: this.getAuthHeaders()
    }).pipe(
      map((response: any[]) => response?.map(u => this.normalizeUserProfile(u)) ?? []),
      tap(response => console.log('Pending customers loaded:', response)),
      catchError(error => {
        console.error('Error loading pending customers:', error);
        throw error;
      })
    );
  }

  activateAdmin(id: number): Observable<string> {
    return this.http.post(
      `${this.apiUrl}/api/admin/activate-admin/${id}`,
      {},
      {
        headers: this.getAuthHeaders(),
        responseType: 'text' 
      }
    ).pipe(
      tap(response => console.log('Admin activated successfully:', response)),
      catchError(error => {
        console.error('Failed to activate admin:', error);
        throw error;
      })
    );
  }
  

  activateCustomer(id: number): Observable<string> {
    return this.http.post(`${this.apiUrl}/api/admin/activate-customer/${id}`, {}, {
      headers: this.getAuthHeaders(),responseType: 'text'
    }).pipe(
      tap(response => console.log('Customer activated successfully:', response)),
      catchError(error => {
        console.error('Error activating customer:', error);
        throw error;
      })
    );
  }

  deactivateUser(id: number): Observable<string> {
    return this.http.post(`${this.apiUrl}/api/admin/deactivate-user/${id}`, {}, {
      headers: this.getAuthHeaders(),responseType: 'text'
    }).pipe(
      tap(response => console.log('User deactivated successfully:', response)),
      catchError(error => {
        console.error('Error deactivating user:', error);
        throw error;
      })
    );
  }

  findUser(email?: string, id?: number): Observable<UserProfileResponse> {
    let params = '';
    if (email) {
      params = `?email=${email}`;
    } else if (id) {
      params = `?id=${id}`;
    }
    return this.http.get<UserProfileResponse>(`${this.apiUrl}/api/admin/find-user${params}`, {
      headers: this.getAuthHeaders()
    }).pipe(
      map((response: any) => this.normalizeUserProfile(response)),
      tap(response => console.log('User found:', response)),
      catchError(error => {
        console.error('Error finding user:', error);
        throw error;
      })
    );
  }

  getAllUsers(): Observable<UserProfileResponse[]> {
    return this.http.get<UserProfileResponse[]>(`${this.apiUrl}/api/admin/all-users`, {
      headers: this.getAuthHeaders()
    }).pipe(
      map((response: any[]) => response?.map(u => this.normalizeUserProfile(u)) ?? []),
      tap(response => console.log('All users loaded:', response)),
      catchError(error => {
        console.error('Error loading all users:', error);
        throw error;
      })
    );
  }

  private setCurrentUser(user: LoginResponse): void {
    localStorage.setItem('currentUser', JSON.stringify(user));
    this.currentUserSubject.next(user);
  }

  logout(): void {
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
  }

  getCurrentUser(): LoginResponse | null {
    return this.currentUserSubject.value;
  }

  isLoggedIn(): boolean {
    return this.getCurrentUser() !== null;
  }

  isAdmin(): boolean {
    const user = this.getCurrentUser();
    return user?.role === Roles.ADMIN;
  }

  isCustomer(): boolean {
    const user = this.getCurrentUser();
    return user?.role === Roles.CUSTOMER;
  }

  getAuthHeaders(): HttpHeaders {
    const user = this.getCurrentUser();
    return new HttpHeaders({
      'Authorization': `Bearer ${user?.token}`
    });
  }

  // Ensure the frontend always receives `isActive` boolean
  private normalizeUserProfile(user: any): UserProfileResponse {
    if (!user) { return user; }
    const normalized: any = { ...user };
    // Some backends serialize boolean fields as `active` instead of `isActive`
    if (typeof normalized.isActive === 'undefined' && typeof normalized.active !== 'undefined') {
      normalized.isActive = normalized.active;
    }
    // Coerce to boolean if it comes as a string
    if (typeof normalized.isActive === 'string') {
      normalized.isActive = normalized.isActive.toLowerCase() === 'true';
    }
    // Default to false if still undefined
    if (typeof normalized.isActive === 'undefined') {
      normalized.isActive = false;
    }
    return normalized as UserProfileResponse;
  }
}
