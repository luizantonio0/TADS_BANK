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

  loadClientes(): void {
    this.clienteService.getClientesParaAprovar().subscribe((clientes: Cliente[]) => {
      this.clientes = clientes;
    });
  }
  
}
