import { Component, ElementRef, HostListener, ViewChild } from '@angular/core';
import { TransactionGroup } from '../../../../shared/models/transaction.model';
import { CurrencyPipe, DatePipe, TitleCasePipe } from '@angular/common';
import { TransactionFeedItem } from '../transaction-feed-item/transaction-feed-item';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'extrato',
  imports: [DatePipe, CurrencyPipe, TitleCasePipe, TransactionFeedItem, ReactiveFormsModule],
  templateUrl: './extrato.html'
})
export class Extrato {

  currentAccount = {
    "account_number": '94853-33',
    "account_name": 'João Silva',
  }

  transactionGroups: TransactionGroup[] = [
  {
    day: '2026-03-13T00:00:00.000Z',
    balance: 4725.60,
    transactions: []
  },
  {
    day: '2026-03-12T00:00:00.000Z',
    balance: 4725.60,
    transactions: [
      { uuid: 'u12-1', type: 'deposit', amount: 1000.00, date: '2026-03-12T09:00:00Z', origin: undefined, destination: undefined },
      { uuid: 'u12-2', type: 'transfer', origin: { account_number: '10293-11', account_name: 'Supermercado Silva' }, destination: { account_number: '94853-33', account_name: 'João Silva' }, amount: 450.00, date: '2026-03-12T11:30:00Z' },
      { uuid: 'u12-3', type: 'transfer', origin: { account_number: '94853-33', account_name: 'João Silva' }, destination: { account_number: '55443-22', account_name: 'Carlos Aluguel' }, amount: 1200.00, date: '2026-03-12T14:15:00Z' },
      { uuid: 'u12-4', type: 'withdraw', amount: 100.00, date: '2026-03-12T18:00:00Z', origin: undefined, destination: undefined }
    ]
  },
  {
    day: '2026-03-11T00:00:00.000Z',
    balance: 4575.60,
    transactions: [
      { uuid: 'u11-1', type: 'transfer', origin: { account_number: '94853-33', account_name: 'João Silva' }, destination: { account_number: '88776-55', account_name: 'Posto de Gasolina' }, amount: 250.00, date: '2026-03-11T08:20:00Z' },
      { uuid: 'u11-2', type: 'deposit', amount: 2000.00, date: '2026-03-11T10:00:00Z', origin: undefined, destination: undefined },
      { uuid: 'u11-3', type: 'transfer', origin: { account_number: '22334-11', account_name: 'Empresa XPTO' }, destination: { account_number: '94853-33', account_name: 'João Silva' }, amount: 3500.00, date: '2026-03-11T13:45:00Z' },
      { uuid: 'u11-4', type: 'withdraw', amount: 50.00, date: '2026-03-11T17:30:00Z', origin: undefined, destination: undefined }
    ]
  },
  {
    day: '2026-03-10T00:00:00.000Z',
    balance: -624.40,
    transactions: [
      { uuid: 'u10-1', type: 'transfer', origin: { account_number: '94853-33', account_name: 'João Silva' }, destination: { account_number: '33221-00', account_name: 'Restaurante Sabor' }, amount: 85.90, date: '2026-03-10T12:10:00Z' },
      { uuid: 'u10-2', type: 'transfer', origin: { account_number: '77665-44', account_name: 'Marcos Oliveira' }, destination: { account_number: '94853-33', account_name: 'João Silva' }, amount: 150.00, date: '2026-03-10T14:00:00Z' },
      { uuid: 'u10-3', type: 'withdraw', amount: 300.00, date: '2026-03-10T16:20:00Z', origin: undefined, destination: undefined },
      { uuid: 'u10-4', type: 'deposit', amount: 50.00, date: '2026-03-10T19:00:00Z', origin: undefined, destination: undefined }
    ]
  },
  {
    day: '2026-03-09T00:00:00.000Z',
    balance: -438.50,
    transactions: [
      { uuid: 'u09-1', type: 'withdraw', amount: 150.00, date: '2026-03-09T09:15:00Z', origin: undefined, destination: undefined },
      { uuid: 'u09-2', type: 'transfer', origin: { account_number: '94853-33', account_name: 'João Silva' }, destination: { account_number: '11998-77', account_name: 'Farmácia Preço Baixo' }, amount: 42.50, date: '2026-03-09T11:40:00Z' },
      { uuid: 'u09-3', type: 'transfer', origin: { account_number: '99887-11', account_name: 'Tati Machado' }, destination: { account_number: '94853-33', account_name: 'João Silva' }, amount: 200.00, date: '2026-03-09T15:00:00Z' },
      { uuid: 'u09-4', type: 'deposit', amount: 100.00, date: '2026-03-09T20:10:00Z', origin: undefined, destination: undefined }
    ]
  },
  {
    day: '2026-03-08T00:00:00.000Z',
    balance: -546.00,
    transactions: [
      { uuid: 'u08-1', type: 'transfer', origin: { account_number: '55667-88', account_name: 'Felipe Neto' }, destination: { account_number: '94853-33', account_name: 'João Silva' }, amount: 1500.00, date: '2026-03-08T10:00:00Z' },
      { uuid: 'u08-2', type: 'withdraw', amount: 200.00, date: '2026-03-08T13:00:00Z', origin: undefined, destination: undefined },
      { uuid: 'u08-3', type: 'transfer', origin: { account_number: '94853-33', account_name: 'João Silva' }, destination: { account_number: '44332-11', account_name: 'Imobiliária House' }, amount: 2800.00, date: '2026-03-08T16:30:00Z' },
      { uuid: 'u08-4', type: 'deposit', amount: 50.00, date: '2026-03-08T19:45:00Z', origin: undefined, destination: undefined }
    ]
  },
  {
    day: '2026-03-07T00:00:00.000Z',
    balance: 904.00,
    transactions: [
      { uuid: 'u07-1', type: 'deposit', amount: 800.00, date: '2026-03-07T08:30:00Z', origin: undefined, destination: undefined },
      { uuid: 'u07-2', type: 'transfer', origin: { account_number: '22113-99', account_name: 'Loja de Roupas' }, destination: { account_number: '94853-33', account_name: 'João Silva' }, amount: 300.00, date: '2026-03-07T11:00:00Z' },
      { uuid: 'u07-3', type: 'withdraw', amount: 100.00, date: '2026-03-07T15:20:00Z', origin: undefined, destination: undefined },
      { uuid: 'u07-4', type: 'transfer', origin: { account_number: '94853-33', account_name: 'João Silva' }, destination: { account_number: '12345-66', account_name: 'Beatriz Silva' }, amount: 50.00, date: '2026-03-07T20:15:00Z' }
    ]
  },
  {
    day: '2026-03-06T00:00:00.000Z',
    balance: -46.00,
    transactions: [
      { uuid: 'u06-1', type: 'deposit', amount: 500.00, date: '2026-03-06T09:00:00Z', origin: undefined, destination: undefined },
      { uuid: 'u06-2', type: 'transfer', origin: { account_number: '94853-33', account_name: 'João Silva' }, destination: { account_number: '00112-33', account_name: 'Padaria Pão Quente' }, amount: 46.00, date: '2026-03-06T12:00:00Z' },
      { uuid: 'u06-3', type: 'transfer', origin: { account_number: '66554-11', account_name: 'Rodrigo Faro' }, destination: { account_number: '94853-33', account_name: 'João Silva' }, amount: 1000.00, date: '2026-03-06T15:30:00Z' },
      { uuid: 'u06-4', type: 'withdraw', amount: 1500.00, date: '2026-03-06T21:00:00Z', origin: undefined, destination: undefined }
    ]
  }
  ];

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
