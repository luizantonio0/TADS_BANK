import { Component } from '@angular/core';
import { ModalEditarGerente } from '../modal-editar-gerente/modal-editar-gerente';
import { ModalExcluirGerente } from '../modal-excluir-gerente/modal-excluir-gerente';

@Component({
  selector: 'tab-manter-gerentes',
  imports: [ModalEditarGerente, ModalExcluirGerente],
  templateUrl: './tab-manter-gerentes.html',
  styleUrl: './tab-manter-gerentes.css',
})
export class TabManterGerentes {

  exibirModalEditar: boolean = false;
  exibirModalExcluir: boolean = false;

  abrirModalEdicao() {
    this.exibirModalEditar = true;
  }

  abrirModalExcluir() {
    this.exibirModalExcluir = true;
  }
}
