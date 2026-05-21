import { Component, OnInit } from '@angular/core';
import { DataGridComponent } from "../../../components/data-grid/data-grid";
import { DataGridColumns, DataGridRequest, DataGridResponse } from '../../../shared/models/datagrid.model';
import { Cliente } from '../../../shared/models/cliente.model';
import { CurrencyPipe } from '@angular/common';
import { CpfPipe } from '../../../shared/pipe/cpf.pipe';
import { ClienteService } from '../../../shared/service/requests/cliente.service';

@Component({
  selector: 'tab-solicitacoes',
  imports: [DataGridComponent, CurrencyPipe, CpfPipe],
  templateUrl: './tab-solicitacoes.html'
})
export class TabSolicitacoes implements OnInit {

  clientes: Cliente[] = [];

  colunas: DataGridColumns[] = [
    { size: 20 , title: 'CPF' },
    { size: 50 , title: 'Nome' },
    { size: 10 , title: 'Salário' },
    { size: 20 , title: 'Ações' } 
  ]

  constructor(private clienteService: ClienteService) {}

  ngOnInit(): void {
    this.loadClientes();
  }

  supplier = async (req: DataGridRequest<Cliente, unknown>): Promise<DataGridResponse<Cliente>> => {
    if (!this.clientes.length) {
      await this.loadClientes();
    }

    const start = (req.page - 1) * req.page_size;
    const end = start + req.page_size;

    return {
      total_count: this.clientes.length,
      data: this.clientes.slice(start, end),
    };
  };

  loadClientes(): Promise<void> {
    return new Promise((resolve, reject) => {
      this.clienteService.getClientesParaAprovar().subscribe({
        next: (clientes: Cliente[]) => {
          this.clientes = clientes;
          resolve();
        },
        error: reject,
      });
    });
  }
  
}
