import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TabRelatorioClientes } from './tab-relatorio-clientes';

describe('TabRelatorioClientes', () => {
  let component: TabRelatorioClientes;
  let fixture: ComponentFixture<TabRelatorioClientes>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TabRelatorioClientes],
    }).compileComponents();

    fixture = TestBed.createComponent(TabRelatorioClientes);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
