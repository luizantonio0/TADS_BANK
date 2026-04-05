import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InicialAdministrador } from './inicial-administrador';

describe('InicialAdministrador', () => {
  let component: InicialAdministrador;
  let fixture: ComponentFixture<InicialAdministrador>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InicialAdministrador],
    }).compileComponents();

    fixture = TestBed.createComponent(InicialAdministrador);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
