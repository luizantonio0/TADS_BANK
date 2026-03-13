import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TabClientes } from './tab-clientes';

describe('TabClientes', () => {
  let component: TabClientes;
  let fixture: ComponentFixture<TabClientes>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TabClientes],
    }).compileComponents();

    fixture = TestBed.createComponent(TabClientes);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
