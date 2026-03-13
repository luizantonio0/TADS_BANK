import { NgClass } from '@angular/common';
import { Component } from '@angular/core';
import { TabSolicitacoes } from './tab-solicitacoes/tab-solicitacoes';
import { TabClientes } from './tab-clientes/tab-clientes';
import { TabMelhoresClientes } from './tab-melhores-clientes/tab-melhores-clientes';

@Component({
  selector: 'app-inicial-gerente',
  imports: [NgClass, TabClientes, TabSolicitacoes, TabMelhoresClientes],
  templateUrl: './inicial-gerente.html'
})
export class InicialGerente {
  selectedTab = 1;
}
