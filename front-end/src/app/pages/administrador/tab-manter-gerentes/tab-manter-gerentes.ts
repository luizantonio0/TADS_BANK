import { Component } from '@angular/core';
import { ModalEditarGerente } from '../modal-editar-gerente/modal-editar-gerente';

@Component({
  selector: 'tab-manter-gerentes',
  imports: [ModalEditarGerente],
  templateUrl: './tab-manter-gerentes.html',
  styleUrl: './tab-manter-gerentes.css',
})
export class TabManterGerentes {

  exibirModalEditar: boolean = false;

  abrirModalEdicao() {
    this.exibirModalEditar = true;
  }

}
