import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModalDepositarSacar } from './modal-depositar-sacar';

describe('ModalDepositarSacar', () => {
  let component: ModalDepositarSacar;
  let fixture: ComponentFixture<ModalDepositarSacar>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModalDepositarSacar],
    }).compileComponents();

    fixture = TestBed.createComponent(ModalDepositarSacar);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
