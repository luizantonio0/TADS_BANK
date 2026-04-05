import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModalConfirmarLogout } from './modal-confirmar-logout';

describe('ModalConfirmarLogout', () => {
  let component: ModalConfirmarLogout;
  let fixture: ComponentFixture<ModalConfirmarLogout>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModalConfirmarLogout],
    }).compileComponents();

    fixture = TestBed.createComponent(ModalConfirmarLogout);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
