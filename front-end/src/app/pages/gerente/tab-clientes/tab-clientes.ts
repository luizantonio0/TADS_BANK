import { Component, ViewChild } from '@angular/core';
import { DataGridColumns, DataGridRequest, DataGridResponse } from '../../../shared/models/datagrid.model';
import { Cliente } from '../../../shared/models/cliente.model';
import { DataGridComponent } from "../../../components/data-grid/data-grid";
import { CurrencyPipe, NgClass } from '@angular/common';
import { CpfPipe } from '../../../shared/pipe/cpf.pipe';
import { ModalFiltrarCliente } from '../modal-filtrar-cliente/modal-filtrar-cliente';
import { firstValueFrom } from 'rxjs';
import { LoadingService } from '../../../shared/service/loading.service';
import { ToastService } from '../../../shared/service/toast/toast';
import { ClienteService } from '../../../shared/service/requests/cliente.service';

@Component({
  selector: 'tab-clientes',
  imports: [DataGridComponent, CurrencyPipe, CpfPipe, ModalFiltrarCliente, NgClass],
  templateUrl: './tab-clientes.html'
})
export class TabClientes {

    filtrando = false;
    nomeCliente = '';
    @ViewChild('gridClientes') dataGrid!: DataGridComponent<any, any>;
  
    colunas: DataGridColumns[] = [
      { size: 15 , title: 'CPF' },
      { size: 25 , title: 'Nome' },
      { size: 20 , title: 'Cidade' },
      { size: 16 , title: 'Saldo' },
      { size: 14 , title: 'Limite' },
      { size: 10 , title: 'Ações' } 
    ]

    constructor(private clienteService: ClienteService, private toastService: ToastService, private loadingService: LoadingService) {}
  
    loadClientes(): Promise<Cliente[]> {
      return this.loadingService.withLoading(() => firstValueFrom(this.clienteService.getClientes(this.nomeCliente)).catch((httpError) => {
        this.toastService.error(httpError.error?.error || "Algo deu errado");
        throw httpError;
      }));
    }

    onFiltered(nome: string) {
      this.nomeCliente = nome;
      this.dataGrid.fetch(false);
      this.toastService.success('Filtros aplicados com sucesso!');
    }
    
}
