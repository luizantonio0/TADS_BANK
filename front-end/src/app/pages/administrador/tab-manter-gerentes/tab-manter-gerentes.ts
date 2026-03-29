import { Component } from '@angular/core';
import { ModalEditarGerente } from '../modal-editar-gerente/modal-editar-gerente';
import { ModalExcluirGerente } from '../modal-excluir-gerente/modal-excluir-gerente';
import { ModalAdicionarGerente } from '../modal-adicionar-gerente/modal-adicionar-gerente';

@Component({
  selector: 'tab-manter-gerentes',
  imports: [ModalEditarGerente, ModalExcluirGerente, ModalAdicionarGerente],
  templateUrl: './tab-manter-gerentes.html',
  styleUrl: './tab-manter-gerentes.css',
})
export class TabManterGerentes {

  exibirModalEditar: boolean = false;
  exibirModalExcluir: boolean = false;
  exibirModalAdicionar: boolean = false;

  abrirModalEdicao() {
    this.exibirModalEditar = true;
  }

  abrirModalExcluir() {
    this.exibirModalExcluir = true;
  }

  abrirModalAdicionar() {
    this.exibirModalAdicionar = true;
  }
}
