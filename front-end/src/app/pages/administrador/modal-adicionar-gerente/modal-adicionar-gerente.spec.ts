import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModalAdicionarGerente } from './modal-adicionar-gerente';

describe('ModalAdicionarGerente', () => {
  let component: ModalAdicionarGerente;
  let fixture: ComponentFixture<ModalAdicionarGerente>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModalAdicionarGerente],
    }).compileComponents();

    fixture = TestBed.createComponent(ModalAdicionarGerente);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
