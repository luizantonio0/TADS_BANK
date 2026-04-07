import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModalEditarGerente } from './modal-editar-gerente';

describe('ModalEditarGerente', () => {
  let component: ModalEditarGerente;
  let fixture: ComponentFixture<ModalEditarGerente>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModalEditarGerente],
    }).compileComponents();

    fixture = TestBed.createComponent(ModalEditarGerente);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
