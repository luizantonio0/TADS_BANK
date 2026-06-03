import { Component, Input } from '@angular/core';
import { CurrencyPipe, DatePipe, NgClass } from '@angular/common';
import { Movimentacao } from '../../../shared/models/conta.model';

@Component({
  selector: 'transaction-feed-item',
  imports: [CurrencyPipe, DatePipe, NgClass],
  templateUrl: './transaction-feed-item.html',
})
export class TransactionFeedItem {
  @Input() accountNumber!: string;
  @Input({ required: true }) transaction!: Movimentacao;

  isSaida() {
    return this.transaction.tipo === 'saque' || (this.transaction.tipo === 'transferência' && this.transaction.origem == this.accountNumber);
  }

}
