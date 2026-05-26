import { Component, Input } from '@angular/core';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { Movimentacao } from '../../../shared/models/conta.model';

@Component({
  selector: 'transaction-feed-item',
  imports: [CurrencyPipe, DatePipe],
  templateUrl: './transaction-feed-item.html',
})
export class TransactionFeedItem {
  @Input() accountNumber!: string;
  @Input({ required: true }) transaction!: Movimentacao;
}
