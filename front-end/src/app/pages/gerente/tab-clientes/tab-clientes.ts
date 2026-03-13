import { Component } from '@angular/core';
import { DataGridColumns, DataGridRequest, DataGridResponse } from '../../../shared/models/datagrid.model';
import { Cliente } from '../../../shared/models/cliente.model';
import { DataGridComponent } from "../../../components/data-grid/data-grid";
import { CurrencyPipe } from '@angular/common';
import { CpfPipe } from '../../../shared/pipe/cpf.pipe';
import { ModalFiltrarCliente } from '../modal-filtrar-cliente/modal-filtrar-cliente';

@Component({
  selector: 'tab-clientes',
  imports: [DataGridComponent, CurrencyPipe, CpfPipe, ModalFiltrarCliente],
  templateUrl: './tab-clientes.html'
})
export class TabClientes {
  clientes = Array.from({ length: 25 }, (_, i) => ({
      cpf: Math.random().toString().slice(2, 13).padEnd(11, '0'),
      nome: `Cliente ${i + 1}`,
      email: `cliente${i + 1}@email.com.br`,
      salario: Math.floor(Math.random() * (15000 - 2000 + 1)) + 2000,
      saldo: Math.floor(Math.random() * (15000 - 2000 + 1)) + 2000,
      limite: Math.floor(Math.random() * (15000 - 2000 + 1)) + 2000,
      endereco: `Rua Exemplo, nr ${100 + i}`,
      cidade: ["Curitiba", "São Paulo", "Porto Alegre", "Belo Horizonte"][i % 4],
      estado: ["PR", "SP", "RS", "MG"][i % 4]
    }));
    
    filtrando = false;
  
    colunas: DataGridColumns[] = [
      { size: 15 , title: 'CPF' },
      { size: 25 , title: 'Nome' },
      { size: 20 , title: 'Cidade' },
      { size: 10 , title: 'Estado' },
      { size: 10 , title: 'Saldo' },
      { size: 10 , title: 'Limite' },
      { size: 10 , title: 'Ações' } 
    ]
  
    supplier = (req: DataGridRequest<Cliente, any>): Promise<DataGridResponse<Cliente>> => {
      return Promise.resolve({
        total_count: 25,
        data: this.clientes.slice(req.page * req.page_size - req.page_size, req.page * req.page_size)
      })
    }
}
