import { Component, OnInit, ViewChild } from '@angular/core';
import { DataGridComponent } from "../../../components/data-grid/data-grid";
import { DataGridColumns, DataGridRequest, DataGridResponse } from '../../../shared/models/datagrid.model';
import { Cliente } from '../../../shared/models/cliente.model';
import { CurrencyPipe } from '@angular/common';
import { CpfPipe } from '../../../shared/pipe/cpf.pipe';
import { ClienteService } from '../../../shared/service/requests/cliente.service';
import { first, firstValueFrom } from 'rxjs';
import { ToastService } from '../../../shared/service/toast/toast';
import { LoadingService } from '../../../shared/service/loading.service';
import { ModalRejeitarCliente } from '../modal-rejeitar-cliente/modal-rejeitar-cliente';

@Component({
  selector: 'tab-solicitacoes',
  imports: [DataGridComponent, CurrencyPipe, CpfPipe, ModalRejeitarCliente],
  templateUrl: './tab-solicitacoes.html'
})
export class TabSolicitacoes {

  @ViewChild('gridSolicitacoes') dataGrid!: DataGridComponent<any, any>;
  clientes: Cliente[] = [];

  rejeitando: Cliente | undefined;

  colunas: DataGridColumns[] = [
    { size: 20, title: 'CPF' },
    { size: 50, title: 'Nome' },
    { size: 10, title: 'Salário' },
    { size: 20, title: 'Ações' }
  ]

  constructor(private clienteService: ClienteService, private toastService: ToastService, private loadingService: LoadingService) { }

  onRejeitar(cliente: Cliente) {
    this.rejeitando = cliente;
  }

  onRejeitarClosed()  {
    this.rejeitando = undefined;
  }

  loadClientes(): Promise<Cliente[]> {
    return firstValueFrom(this.clienteService.getClientesParaAprovar()).catch((httpError) => {
      this.toastService.error(httpError.error?.error || "Algo deu errado");
      throw httpError;
    });
  }

  aprovarCliente(cliente: Cliente): Promise<any> {
    return this.loadingService.withLoading(() => firstValueFrom(this.clienteService.aprovarCliente(cliente.cpf))
      .then(_ => {
        this.toastService.success("Cliente aprovado com sucesso!");
        this.dataGrid.fetch(false);
      })
      .catch((httpError) => {
        this.toastService.error(httpError.error?.error || "Algo deu errado");
        throw httpError;
      }));
  }

  rejeitarCliente(motivo: string): Promise<any> {
    if(!this.rejeitando) {
      return Promise.resolve();
    }
    return this.loadingService.withLoading(() =>
      firstValueFrom(this.clienteService.rejeitarCliente(this.rejeitando!.cpf, motivo))
        .then((_) => {
          this.toastService.success('Cliente rejeitado com sucesso!');
          this.dataGrid.fetch(false);
        })
        .catch((httpError) => {
          this.toastService.error(httpError.error?.error || 'Algo deu errado');
          throw httpError;
        }),
    );
  }

}
