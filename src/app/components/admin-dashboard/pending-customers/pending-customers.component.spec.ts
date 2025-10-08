import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PendingCustomersComponent } from './pending-customers.component';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../services/auth.service';

describe('PendingCustomersComponent', () => {
  let component: PendingCustomersComponent;
  let fixture: ComponentFixture<PendingCustomersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PendingCustomersComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PendingCustomersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  
  
});
