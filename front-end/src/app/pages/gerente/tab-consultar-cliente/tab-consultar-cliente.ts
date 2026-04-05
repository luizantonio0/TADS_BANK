import { Component } from '@angular/core';
import { FormsModule, NgModel } from '@angular/forms';
import { NgxMaskDirective } from 'ngx-mask';
import { Cliente } from '../../../shared/models/cliente.model';
import { CpfPipe } from '../../../shared/pipe/cpf.pipe';
import { CurrencyPipe } from '@angular/common';
import { TelefonePipe } from '../../../shared/pipe/telefone.pipe';

@Component({
  selector: 'tab-consultar-cliente',
  imports: [NgxMaskDirective, FormsModule, CpfPipe, CurrencyPipe, TelefonePipe],
  templateUrl: './tab-consultar-cliente.html'
})
export class TabConsultarCliente {
  cpf = "";
  cliente: Cliente | null = null;

  submit() {
    this.cliente = Array.from({ length: 1 }, (_, i) => ({
      cpf: Math.random().toString().slice(2, 13).padEnd(11, '0'),
      nome: `Cliente ${i + 1}`,
      email: `cliente${i + 1}@email.com.br`,
      salario: Math.floor(Math.random() * (15000 - 2000 + 1)) + 2000,
      saldo: Math.floor(Math.random() * (15000 - 2000 + 1)) + 2000,
      limite: Math.floor(Math.random() * (15000 - 2000 + 1)) + 2000,
      conta: 6326,
      telefone: '19984209245',
      gerente_nome: 'Edmar Pires',
      gerente_email: 'edmar@tadbank.com',
      endereco: `Rua Exemplo, nr ${100 + i}`,
      cidade: ["Curitiba", "São Paulo", "Porto Alegre", "Belo Horizonte"][i % 4],
      estado: ["PR", "SP", "RS", "MG"][i % 4]
    }))[0]
  }
}
