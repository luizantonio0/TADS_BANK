import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TabDashboardAdmin } from './tab-dashboard-admin';

describe('TabDashboardAdmin', () => {
  let component: TabDashboardAdmin;
  let fixture: ComponentFixture<TabDashboardAdmin>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TabDashboardAdmin],
    }).compileComponents();

    fixture = TestBed.createComponent(TabDashboardAdmin);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
