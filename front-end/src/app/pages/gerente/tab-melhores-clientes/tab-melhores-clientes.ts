import { CurrencyPipe, NgClass } from '@angular/common';
import { Component } from '@angular/core';
import { CpfPipe } from '../../../shared/pipe/cpf.pipe';

@Component({
  selector: 'tab-melhores-clientes',
  imports: [NgClass, CpfPipe, CurrencyPipe],
  templateUrl: './tab-melhores-clientes.html'
})
export class TabMelhoresClientes {

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


}
