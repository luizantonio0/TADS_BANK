import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InicialGerente } from './inicial-gerente';

describe('InicialGerente', () => {
  let component: InicialGerente;
  let fixture: ComponentFixture<InicialGerente>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InicialGerente],
    }).compileComponents();

    fixture = TestBed.createComponent(InicialGerente);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
