import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { ModalEditarGerente } from '../modal-editar-gerente/modal-editar-gerente';
import { ModalExcluirGerente } from '../modal-excluir-gerente/modal-excluir-gerente';
import { ModalAdicionarGerente } from '../modal-adicionar-gerente/modal-adicionar-gerente';
import { GerenteService } from '../../../shared/service/requests/gerente.service';
import { Gerente, GerenteResponse } from '../../../shared/models/gerente.model';
import { CommonModule } from '@angular/common';
import { LoadingService } from '../../../shared/service/loading.service';
import { firstValueFrom } from 'rxjs';
import { ToastService } from '../../../shared/service/toast/toast';

@Component({
  selector: 'tab-manter-gerentes',
  imports: [ModalEditarGerente, ModalExcluirGerente, ModalAdicionarGerente],
  templateUrl: './tab-manter-gerentes.html',
  styleUrl: './tab-manter-gerentes.css',
})
export class TabManterGerentes implements OnInit {
  gerenteService = inject(GerenteService);
  loadService = inject(LoadingService);
  toastService = inject(ToastService);
  cdr = inject(ChangeDetectorRef);
  gerentes: GerenteResponse[] = [];

  exibirModalEditar: boolean = false;
  exibirModalExcluir: boolean = false;
  exibirModalAdicionar: boolean = false;

  ngOnInit(): void {
    this.carregarGerentes();
  }

  carregarGerentes(): Promise<GerenteResponse[]> {
    return this.loadService.withLoading(() =>
      firstValueFrom(this.gerenteService.getGerentes()).catch((httpError) => {
        this.toastService.error(httpError.error?.error || 'Algo deu errado');
        throw httpError;
      }),
    );
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
