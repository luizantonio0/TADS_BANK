import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TabConsultarCliente } from './tab-consultar-cliente';

describe('TabConsultarCliente', () => {
  let component: TabConsultarCliente;
  let fixture: ComponentFixture<TabConsultarCliente>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TabConsultarCliente],
    }).compileComponents();

    fixture = TestBed.createComponent(TabConsultarCliente);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
