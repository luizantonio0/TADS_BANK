import { Component, OnInit } from '@angular/core';
import { DataGridComponent } from "../../../components/data-grid/data-grid";
import { DataGridColumns, DataGridRequest, DataGridResponse } from '../../../shared/models/datagrid.model';
import { Cliente } from '../../../shared/models/cliente.model';
import { CurrencyPipe } from '@angular/common';
import { CpfPipe } from '../../../shared/pipe/cpf.pipe';
import { ClienteService } from '../../../shared/service/requests/cliente.service';
import { firstValueFrom } from 'rxjs';
import { ToastService } from '../../../shared/service/toast/toast';

@Component({
  selector: 'tab-solicitacoes',
  imports: [DataGridComponent, CurrencyPipe, CpfPipe],
  templateUrl: './tab-solicitacoes.html'
})
export class TabSolicitacoes {

  clientes: Cliente[] = [];

  colunas: DataGridColumns[] = [
    { size: 20 , title: 'CPF' },
    { size: 50 , title: 'Nome' },
    { size: 10 , title: 'Salário' },
    { size: 20 , title: 'Ações' } 
  ]

  constructor(private clienteService: ClienteService, private toastService: ToastService) {}

  loadClientes(): Promise<Cliente[]> {
    return firstValueFrom(this.clienteService.getClientesParaAprovar()).catch((httpError) => {
      this.toastService.error(httpError.error?.error || "Algo deu errado");
      throw httpError;
    });
  }

  aprovarCliente(cliente: Cliente): Promise<any> {
    return firstValueFrom(this.clienteService.aprovarCliente(cliente.cpf)).catch((httpError) => {
      this.toastService.error(httpError.error?.error || "Algo deu errado");
      throw httpError;
    });
  }

  rejeitarCliente(cliente: Cliente): Promise<any> {
    return firstValueFrom(this.clienteService.rejeitarCliente(cliente.cpf)).catch((httpError) => {
      this.toastService.error(httpError.error?.error || "Algo deu errado");
      throw httpError;
    });
  }
  
}
