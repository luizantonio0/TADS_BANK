import { Component } from '@angular/core';
import { DataGridComponent } from "../../../components/data-grid/data-grid";
import { DataGridColumns, DataGridRequest, DataGridResponse } from '../../../shared/models/datagrid.model';
import { Cliente } from '../../../shared/models/cliente.model';
import { CurrencyPipe } from '@angular/common';
import { CpfPipe } from '../../../shared/pipe/cpf.pipe';

@Component({
  selector: 'tab-solicitacoes',
  imports: [DataGridComponent, CurrencyPipe, CpfPipe],
  templateUrl: './tab-solicitacoes.html'
})
export class TabSolicitacoes {

  clientes = Array.from({ length: 25 }, (_, i) => ({
    cpf: Math.random().toString().slice(2, 13).padEnd(11, '0'),
    nome: `Cliente ${i + 1}`,
    email: `cliente${i + 1}@email.com.br`,
    salario: Math.floor(Math.random() * (15000 - 2000 + 1)) + 2000,
    endereco: `Rua Exemplo, nr ${100 + i}`,
    cidade: ["Curitiba", "São Paulo", "Porto Alegre", "Belo Horizonte"][i % 4],
    estado: ["PR", "SP", "RS", "MG"][i % 4]
  }));

  colunas: DataGridColumns[] = [
    { size: 20 , title: 'CPF' },
    { size: 50 , title: 'Nome' },
    { size: 10 , title: 'Salário' },
    { size: 20 , title: 'Ações' } 
  ]

  supplier = (req: DataGridRequest<Cliente, any>): Promise<DataGridResponse<Cliente>> => {
    return Promise.resolve({
      total_count: 25,
      data: this.clientes.slice(req.page * req.page_size - req.page_size, req.page * req.page_size)
    })
  }
}
