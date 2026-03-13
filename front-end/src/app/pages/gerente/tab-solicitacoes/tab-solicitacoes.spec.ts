import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TabSolicitacoes } from './tab-solicitacoes';

describe('TabSolicitacoes', () => {
  let component: TabSolicitacoes;
  let fixture: ComponentFixture<TabSolicitacoes>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TabSolicitacoes],
    }).compileComponents();

    fixture = TestBed.createComponent(TabSolicitacoes);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
