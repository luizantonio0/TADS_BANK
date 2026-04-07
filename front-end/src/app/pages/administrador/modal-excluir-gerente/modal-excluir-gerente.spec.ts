import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModalExcluirGerente } from './modal-excluir-gerente';

describe('ModalExcluirGerente', () => {
  let component: ModalExcluirGerente;
  let fixture: ComponentFixture<ModalExcluirGerente>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModalExcluirGerente],
    }).compileComponents();

    fixture = TestBed.createComponent(ModalExcluirGerente);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
