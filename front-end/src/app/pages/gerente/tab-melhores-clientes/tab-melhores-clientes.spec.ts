import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TabMelhoresClientes } from './tab-melhores-clientes';

describe('TabMelhoresClientes', () => {
  let component: TabMelhoresClientes;
  let fixture: ComponentFixture<TabMelhoresClientes>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TabMelhoresClientes],
    }).compileComponents();

    fixture = TestBed.createComponent(TabMelhoresClientes);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
