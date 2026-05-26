import { Component, ElementRef, HostListener, Input, OnInit, ViewChild, OnChanges, SimpleChanges, ChangeDetectorRef } from '@angular/core';
import { GrupoMovimentacao } from '../../../shared/models/transaction.model';
import { CurrencyPipe, DatePipe, TitleCasePipe } from '@angular/common';
import { TransactionFeedItem } from '../transaction-feed-item/transaction-feed-item';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { LoadingService } from '../../../shared/service/loading.service';
import { ContaService } from '../../../shared/service/requests/conta.service';
import { NgxMaskDirective } from 'ngx-mask';

@Component({
  selector: 'extrato',
  imports: [DatePipe, CurrencyPipe, TitleCasePipe, TransactionFeedItem, ReactiveFormsModule, NgxMaskDirective],
  templateUrl: './extrato.html'
})
export class Extrato implements OnInit, OnChanges {
  @Input() accountNumber = '';
  @Input() accountName = '';

  transactionGroups: GrupoMovimentacao[] = [];

  isFiltroTooltipVisible = false;

  @ViewChild('filtroTooltipRef') filtroTooltipRef!: ElementRef;

  formFiltroData: FormGroup;

  constructor(
    private fb: FormBuilder,
    private loadService: LoadingService,
    private contaService: ContaService,
    private cdRef: ChangeDetectorRef
  ) {
    this.formFiltroData = this.fb.group({
      dataDe: [''],
      dataAte: [''],
    });
  }

  ngOnInit(): void {
    this.buscarExtrato();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['accountNumber'] && !changes['accountNumber'].firstChange) {
      this.buscarExtrato();
    }
  }

  buscarExtrato() {
    if (!this.accountNumber) return;

    const de = this.converterDataParaAPI(this.formFiltroData.value.dataDe);
    const ate = this.converterDataParaAPI(this.formFiltroData.value.dataAte);

    this.loadService.withLoadingObservable(this.contaService.extrato(this.accountNumber, de, ate)).subscribe({
      next: res => {
        const groupedMap = res.movimentacoes.reduce((acc, mov) => {
          const date = mov.data_hora.split('T')[0];
          if (!acc[date]) {
            acc[date] = [];
          }
          acc[date].push(mov);
          return acc;
        }, {} as Record<string, any[]>);
        this.transactionGroups = Object.entries(groupedMap).map(([dia, movimentacoes]) => ({
          dia,
          movimentacoes,
          saldo: res.saldos_consolidados[dia] || 0
        })).sort((a, b) => b.dia.localeCompare(a.dia));
        this.cdRef.detectChanges();
      }
    });
  }

  private converterDataParaAPI(data: string): string {
    if (!data || data.length < 10) return '';
    // Converte de dd/MM/yyyy para yyyy-MM-dd
    const [dia, mes, ano] = data.split('/');
    return `${ano}-${mes}-${dia}`;
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
