import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TabManterGerentes } from './tab-manter-gerentes';

describe('TabManterGerentes', () => {
  let component: TabManterGerentes;
  let fixture: ComponentFixture<TabManterGerentes>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TabManterGerentes],
    }).compileComponents();

    fixture = TestBed.createComponent(TabManterGerentes);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
