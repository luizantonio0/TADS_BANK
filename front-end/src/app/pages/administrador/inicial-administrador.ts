import { NgClass } from '@angular/common';
import { Component } from '@angular/core';
import { TabDashboardAdmin } from './tab-dashboard-admin/tab-dashboard-admin';
import { TabRelatorioClientes } from './tab-relatorio-clientes/tab-relatorio-clientes';
import { TabManterGerentes } from './tab-manter-gerentes/tab-manter-gerentes';
@Component({
  selector: 'app-inicial-administrador',
  imports: [NgClass, TabDashboardAdmin, TabRelatorioClientes, TabManterGerentes],
  templateUrl: './inicial-administrador.html',
})
export class InicialAdministrador {
  selectedTab = 1;
}
