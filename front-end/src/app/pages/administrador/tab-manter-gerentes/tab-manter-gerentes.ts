import { Component, inject, OnInit } from '@angular/core';
import { ModalEditarGerente } from '../modal-editar-gerente/modal-editar-gerente';
import { ModalExcluirGerente } from '../modal-excluir-gerente/modal-excluir-gerente';
import { ModalAdicionarGerente } from '../modal-adicionar-gerente/modal-adicionar-gerente';
import { GerenteService } from '../../../shared/service/requests/gerente';
import { GerenteResponse } from '../../../shared/models/gerente.model';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'tab-manter-gerentes',
  imports: [ModalEditarGerente, ModalExcluirGerente, ModalAdicionarGerente],
  templateUrl: './tab-manter-gerentes.html',
  styleUrl: './tab-manter-gerentes.css',
})
export class TabManterGerentes implements OnInit {

  gerenteService = inject(GerenteService);
  gerentes: GerenteResponse[] = [];

  exibirModalEditar: boolean = false;
  exibirModalExcluir: boolean = false;
  exibirModalAdicionar: boolean = false;

  ngOnInit(): void {
    this.carregarGerentes();
  }

  carregarGerentes() {
    this.gerenteService.getGerentes().subscribe({
      next: (dados) => {
        this.gerentes = dados;
      },
      error: (erro) => {
        console.error('Erro ao carregar gerentes:', erro);
      }
    });
  }

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
