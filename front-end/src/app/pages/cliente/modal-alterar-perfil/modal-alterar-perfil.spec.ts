import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModalAlterarPerfil } from './modal-alterar-perfil';

describe('ModalAlterarPerfil', () => {
  let component: ModalAlterarPerfil;
  let fixture: ComponentFixture<ModalAlterarPerfil>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModalAlterarPerfil],
    }).compileComponents();

    fixture = TestBed.createComponent(ModalAlterarPerfil);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
