import { CurrencyPipe, NgClass } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CpfPipe } from '../../../shared/pipe/cpf.pipe';
import { Cliente } from '../../../shared/models/cliente.model';
import { ClienteService } from '../../../shared/service/requests/cliente.service';
import { LoadingService } from '../../../shared/service/loading.service';
import { ToastService } from '../../../shared/service/toast/toast';

@Component({
  selector: 'tab-melhores-clientes',
  imports: [NgClass, CpfPipe, CurrencyPipe],
  templateUrl: './tab-melhores-clientes.html'
})
export class TabMelhoresClientes implements OnInit {

  clientes: Cliente[] = [];

  constructor(
    private clienteService: ClienteService, 
    private loadingService: LoadingService, 
    private toastService: ToastService,
    private cdRef: ChangeDetectorRef 
  ) {}

  ngOnInit(): void {
    this.loadingService.withLoadingObservable(this.clienteService.getMelhoresClientes()).subscribe({
      next: (clientes) => {
        this.clientes = clientes;
        this.cdRef.detectChanges();
      },
      error: (error) => {
        this.toastService.error(error.error?.error || "Algo deu errado");
        this.cdRef.detectChanges();
      },
    });
  }


}
