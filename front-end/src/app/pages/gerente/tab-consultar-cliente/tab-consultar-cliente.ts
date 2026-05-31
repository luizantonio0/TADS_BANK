import { ChangeDetectorRef, Component, inject, Input, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgxMaskDirective } from 'ngx-mask';
import { Cliente } from '../../../shared/models/cliente.model';
import { CpfPipe } from '../../../shared/pipe/cpf.pipe';
import { CurrencyPipe } from '@angular/common';
import { TelefonePipe } from '../../../shared/pipe/telefone.pipe';
import { ClienteService } from '../../../shared/service/requests/cliente.service';
import { finalize } from 'rxjs';
import { LoadingService } from '../../../shared/service/loading.service';
import { ToastService } from '../../../shared/service/toast/toast';

@Component({
  selector: 'tab-consultar-cliente',
  imports: [NgxMaskDirective, FormsModule, CpfPipe, CurrencyPipe, TelefonePipe],
  templateUrl: './tab-consultar-cliente.html'
})
export class TabConsultarCliente implements OnInit {
  private clienteService = inject(ClienteService);
  private loadingService = inject(LoadingService);
  private toastService = inject(ToastService);
  private cdRef = inject(ChangeDetectorRef); 

  @Input() cpfInicial: string = '';
  cpf = "";
  cliente: Cliente | null = null;
  buscaRealizada = false;

  ngOnInit(): void {
    if (this.cpfInicial) {
      this.cpf = this.cpfInicial;
      this.submit();
    }
  }

  onCpfChange() {
    this.buscaRealizada = false;
    this.cliente = null;
  }

  submit() {
    if (this.cpf.length < 11) {
      this.toastService.success('Informe um CPF válido.');
      return;
    }

    this.loadingService.withLoadingObservable(this.clienteService.getCliente(this.cpf)).subscribe({
      next: (response) => {
        this.cliente = response;
        this.buscaRealizada = true;
        this.cdRef.detectChanges();
      },
      error: (error) => {
        this.cliente = null;
        this.buscaRealizada = true;
        this.toastService.error(error.error?.error || "Algo deu errado");
        this.cdRef.detectChanges();
      },
    })
    
  }
}
