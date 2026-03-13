import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InicialCliente } from './inicial-cliente';

describe('InicialCliente', () => {
  let component: InicialCliente;
  let fixture: ComponentFixture<InicialCliente>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InicialCliente],
    }).compileComponents();

    fixture = TestBed.createComponent(InicialCliente);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
