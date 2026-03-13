import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TransactionFeedItem } from './transaction-feed-item';

describe('TransactionFeedItem', () => {
  let component: TransactionFeedItem;
  let fixture: ComponentFixture<TransactionFeedItem>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TransactionFeedItem],
    }).compileComponents();

    fixture = TestBed.createComponent(TransactionFeedItem);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
