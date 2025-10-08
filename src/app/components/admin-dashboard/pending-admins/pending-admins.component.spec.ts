import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PendingAdminsComponent } from './pending-admins.component';

describe('PendingAdminsComponent', () => {
  let component: PendingAdminsComponent;
  let fixture: ComponentFixture<PendingAdminsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PendingAdminsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PendingAdminsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
