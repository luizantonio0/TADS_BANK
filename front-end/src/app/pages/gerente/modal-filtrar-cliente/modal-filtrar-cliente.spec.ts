import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModalFiltrarCliente } from './modal-filtrar-cliente';

describe('ModalFiltrarCliente', () => {
  let component: ModalFiltrarCliente;
  let fixture: ComponentFixture<ModalFiltrarCliente>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModalFiltrarCliente],
    }).compileComponents();

    fixture = TestBed.createComponent(ModalFiltrarCliente);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
