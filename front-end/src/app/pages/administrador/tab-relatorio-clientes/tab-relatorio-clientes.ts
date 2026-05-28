import { Component } from '@angular/core';
import { DataGridComponent } from '../../../components/data-grid/data-grid';
import { DataGridColumns } from '../../../shared/models/datagrid.model';
import { Cliente } from '../../../shared/models/cliente.model';
import { LoadingService } from '../../../shared/service/loading.service';
import { ClienteService } from '../../../shared/service/requests/cliente.service';
import { firstValueFrom } from 'rxjs';
import { ToastService } from '../../../shared/service/toast/toast';
import { CurrencyPipe, NgClass } from '@angular/common';
import { CpfPipe } from '../../../shared/pipe/cpf.pipe';

@Component({
  selector: 'tab-relatorio-clientes',
  imports: [DataGridComponent, NgClass, CpfPipe, CurrencyPipe],
  templateUrl: './tab-relatorio-clientes.html',
  styleUrl: './tab-relatorio-clientes.css',
})
export class TabRelatorioClientes {

  columns: DataGridColumns[] = [
    { title: 'CPF', size: 10 },
    { title: 'Nome', size: 10 },
    { title: 'E-mail', size: 10 },
    { title: 'Conta', size: 10 },
    { title: 'Salário', size: 10 },
    { title: 'Saldo', size: 10 },
    { title: 'Limite', size: 10 },
    { title: 'Nome Ger.', size: 10 },
    { title: 'CPF Ger.', size: 10 },
  ]

  constructor(private loadService: LoadingService, private clienteService: ClienteService, private toastService: ToastService) {}

  carregarGerentes(): Promise<Cliente[]> {
    return this.loadService.withLoading(() =>
      firstValueFrom(this.clienteService.relatorioClientes()).catch((httpError) => {
        this.toastService.error(httpError.error?.error || 'Algo deu errado');
        throw httpError;
      }),
    );
  }

}
