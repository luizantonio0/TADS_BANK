import { Component, Input } from '@angular/core';
import { Transaction } from '../../../../shared/models/transaction.model';
import { CurrencyPipe, DatePipe } from '@angular/common';

@Component({
  selector: 'transaction-feed-item',
  imports: [CurrencyPipe, DatePipe],
  templateUrl: './transaction-feed-item.html',
})
export class TransactionFeedItem {
  @Input() accountNumber!: string;
  @Input({ required: true }) transaction!: Transaction;
}
