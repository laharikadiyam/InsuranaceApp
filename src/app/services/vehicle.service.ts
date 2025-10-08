import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { 
  BikeDTO, 
  CarDTO, 
  HealthInsuranceDTO, 
  LifeInsuranceDTO, 
  PremiumCalculationResponse,
  PolicyPurchaseRequest 
} from '../models/vehicle.model';
import { environment } from '../../environments/environment';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class VehicleService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient, private authService: AuthService) { }

  // Bike Insurance
  addBike(bike: BikeDTO): Observable<BikeDTO> {
    return this.http.post<BikeDTO>(`${this.apiUrl}/api/customer/bike/addBike`, bike, {
      headers: this.authService.getAuthHeaders()
    });
  }

  getBikes(): Observable<BikeDTO[]> {
    return this.http.get<BikeDTO[]>(`${this.apiUrl}/api/customer/bike/get`, {
      headers: this.authService.getAuthHeaders()
    });
  }

  updateBike(id: number, bike: BikeDTO): Observable<BikeDTO> {
    return this.http.put<BikeDTO>(`${this.apiUrl}/api/customer/bike/updateBike/${id}`, bike, {
      headers: this.authService.getAuthHeaders()
    });
  }

  deleteBike(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/api/customer/bike/delete/${id}`, {
      headers: this.authService.getAuthHeaders()
    });
  }

  cancelBike(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/api/customer/bike/cancel/${id}`, {
      headers: this.authService.getAuthHeaders()
    });
  }

  cancelConfirmedBike(id: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/api/customer/bike/cancel/${id}`, {}, {
      headers: this.authService.getAuthHeaders()
    });
  }

  confirmBikePurchase(bikeId: number, request: PolicyPurchaseRequest): Observable<BikeDTO> {
    return this.http.post<BikeDTO>(`${this.apiUrl}/api/customer/bike/confirm/${bikeId}`, request, {
      headers: this.authService.getAuthHeaders()
    });
  }

  calculateBikePremium(bike: BikeDTO): Observable<PremiumCalculationResponse> {
    return this.http.post<PremiumCalculationResponse>(`${this.apiUrl}/api/customer/bike/calculate-premium`, bike, {
      headers: this.authService.getAuthHeaders()
    });
  }

  // Car Insurance
  addCar(car: CarDTO): Observable<CarDTO> {
    return this.http.post<CarDTO>(`${this.apiUrl}/api/customer/car/addCar`, car, {
      headers: this.authService.getAuthHeaders()
    });
  }

  getCars(): Observable<CarDTO[]> {
    return this.http.get<CarDTO[]>(`${this.apiUrl}/api/customer/car/get`, {
      headers: this.authService.getAuthHeaders()
    });
  }

  updateCar(id: number, car: CarDTO): Observable<CarDTO> {
    return this.http.put<CarDTO>(`${this.apiUrl}/api/customer/car/updateCar/${id}`, car, {
      headers: this.authService.getAuthHeaders()
    });
  }

  deleteCar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/api/customer/car/delete/${id}`, {
      headers: this.authService.getAuthHeaders()
    });
  }

  cancelCar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/api/customer/car/cancel/${id}`, {
      headers: this.authService.getAuthHeaders()
    });
  }

  cancelConfirmedCar(id: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/api/customer/car/cancel/${id}`, {}, {
      headers: this.authService.getAuthHeaders()
    });
  }

  confirmCarPurchase(carId: number, request: PolicyPurchaseRequest): Observable<CarDTO> {
    return this.http.post<CarDTO>(`${this.apiUrl}/api/customer/car/confirm/${carId}`, request, {
      headers: this.authService.getAuthHeaders()
    });
  }

  calculateCarPremium(car: CarDTO): Observable<PremiumCalculationResponse> {
    return this.http.post<PremiumCalculationResponse>(`${this.apiUrl}/api/customer/car/calculate-premium`, car, {
      headers: this.authService.getAuthHeaders()
    });
  }

  // Health Insurance
  addHealth(health: HealthInsuranceDTO): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/api/customer/health/add`, health, {
      headers: this.authService.getAuthHeaders()
    });
  }

  getHealthInsurances(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/api/customer/health/get`, {
      headers: this.authService.getAuthHeaders()
    });
  }

  updateHealth(id: number, health: HealthInsuranceDTO): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/api/customer/health/update/${id}`, health, {
      headers: this.authService.getAuthHeaders()
    });
  }

  cancelHealth(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/api/customer/health/cancel/${id}`, {
      headers: this.authService.getAuthHeaders()
    });
  }

  confirmHealthPurchase(healthId: number, request: PolicyPurchaseRequest): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/api/customer/health/confirm/${healthId}`, request, {
      headers: this.authService.getAuthHeaders()
    });
  }

  calculateHealthPremium(health: HealthInsuranceDTO): Observable<number> {
    return this.http.post<number>(`${this.apiUrl}/api/customer/health/calculate-premium`, health, {
      headers: this.authService.getAuthHeaders()
    });
  }

  // Life Insurance
  addLife(life: LifeInsuranceDTO): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/api/customer/life/add`, life, {
      headers: this.authService.getAuthHeaders()
    });
  }

  getLifeInsurances(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/api/customer/life/get`, {
      headers: this.authService.getAuthHeaders()
    });
  }

  updateLife(id: number, life: LifeInsuranceDTO): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/api/customer/life/update/${id}`, life, {
      headers: this.authService.getAuthHeaders()
    });
  }

  cancelLife(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/api/customer/life/cancel/${id}`, {
      headers: this.authService.getAuthHeaders()
    });
  }

  confirmLifePurchase(lifeId: number, request: PolicyPurchaseRequest): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/api/customer/life/confirm/${lifeId}`, request, {
      headers: this.authService.getAuthHeaders()
    });
  }

  calculateLifePremium(life: LifeInsuranceDTO): Observable<number> {
    return this.http.post<number>(`${this.apiUrl}/api/customer/life/calculate-premium`, life, {
      headers: this.authService.getAuthHeaders()
    });
  }
}






