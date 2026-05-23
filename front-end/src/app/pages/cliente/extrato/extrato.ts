import { Component, ElementRef, HostListener, Input, ViewChild } from '@angular/core';
import { TransactionGroup } from '../../../shared/models/transaction.model';
import { CurrencyPipe, DatePipe, TitleCasePipe } from '@angular/common';
import { TransactionFeedItem } from '../transaction-feed-item/transaction-feed-item';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'extrato',
  imports: [DatePipe, CurrencyPipe, TitleCasePipe, TransactionFeedItem, ReactiveFormsModule],
  templateUrl: './extrato.html'
})
export class Extrato {
  @Input() accountNumber = '';
  @Input() accountName = '';

  transactionGroups: TransactionGroup[] = [];

  isFiltroTooltipVisible = false;

  @ViewChild('filtroTooltipRef') filtroTooltipRef!: ElementRef;

  formFiltroData: FormGroup;

  constructor(private fb: FormBuilder) {
    this.formFiltroData = this.fb.group({
      dataDe: [''],
      dataAte: [''],
    });
  }

  async aplicarFiltro() {
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    if (!this.isFiltroTooltipVisible) return;
    const clickedInside = this.filtroTooltipRef.nativeElement.contains(event.target);
    if (!clickedInside) {
      this.isFiltroTooltipVisible = false;
    }
  }

  toggleFiltroTooltip(event: MouseEvent) {
    event.stopPropagation();
    this.isFiltroTooltipVisible = !this.isFiltroTooltipVisible;
  }
}
