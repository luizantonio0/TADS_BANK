import { ChangeDetectorRef, Component, OnInit, ViewChild } from '@angular/core';
import { DataGridComponent } from "../../../components/data-grid/data-grid";
import { DataGridColumns } from '../../../shared/models/datagrid.model';
import { LoadingService } from '../../../shared/service/loading.service';
import { ToastService } from '../../../shared/service/toast/toast';
import { GerenteService } from '../../../shared/service/requests/gerente.service';
import { Gerente, GerenteDashboardResponse } from '../../../shared/models/gerente.model';
import { firstValueFrom } from 'rxjs';
import { CurrencyPipe, NgClass } from '@angular/common';
import { CpfPipe } from '../../../shared/pipe/cpf.pipe';
import { ModalAdicionarGerente } from '../modal-adicionar-gerente/modal-adicionar-gerente';
import { ModalExcluirGerente } from '../modal-excluir-gerente/modal-excluir-gerente';
import { ModalEditarGerente } from '../modal-editar-gerente/modal-editar-gerente';
import { EditarGerenteDTO } from '../../../shared/models/resquest/editgerente.model';

@Component({
  selector: 'tab-dashboard-admin',
  imports: [DataGridComponent, CurrencyPipe, CpfPipe, NgClass, ModalEditarGerente, ModalExcluirGerente, ModalAdicionarGerente],
  templateUrl: './tab-dashboard-admin.html',
  styleUrl: './tab-dashboard-admin.css',
})

export class TabDashboardAdmin implements OnInit {

  columns: DataGridColumns[] = [
    { size: 10, title: 'Nome' },
    { size: 10, title: 'CPF' },
    { size: 14, title: 'E-mail' },
    { size: 15, title: 'Total de clientes' },
    { size: 18, title: 'Saldos positivos (+)' },
    { size: 18, title: 'Saldos negativos (-)' },
    { size: 15, title: 'Ações' },
  ]

  exibirModalEditar: boolean = false;
  exibirModalExcluir: boolean = false;
  exibirModalAdicionar: boolean = false;

  @ViewChild('gridGerentes') dataGrid!: DataGridComponent<any, any>;

  gerentes: GerenteDashboardResponse[] = [];
  gerente?: Gerente = undefined;

  constructor(
    private loadService: LoadingService,
    private gerenteService: GerenteService, 
    private toastService: ToastService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.loadService.withLoadingObservable(this.gerenteService.getGerentesDashboard()).subscribe({
      next: (gerentes) => {
        this.gerentes = gerentes;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.toastService.error(err.error?.error || 'Algo deu errado');
      }
    })
  }

  submitCreate(form: any) {
    this.loadService.withLoadingObservable(this.gerenteService.cadastrarGerente(form)).subscribe({
      next: (gerente) => {
        this.toastService.success('Gerente cadastrado com sucesso');
        this.dataGrid.fetch(false);
      },
      error: err => {
        this.toastService.error(err.error?.error || 'Algo deu errado');
      }
    })
  }

  submitEdit(form: EditarGerenteDTO) {
    if(!this.gerente) {
      return;
    }
    this.loadService.withLoadingObservable(this.gerenteService.editarGerente(this.gerente.cpf, form)).subscribe({
      next: (gerente) => {
        this.toastService.success('Gerente cadastrado com sucesso');
        this.dataGrid.fetch(false);
      },
      error: err => {
        this.toastService.error(err.error?.error || 'Algo deu errado');
      }
    })
  }

  submitDelete() {
    debugger
    if(!this.gerente) {
      return;
    }
    this.loadService.withLoadingObservable(this.gerenteService.excluirGerente(this.gerente.cpf)).subscribe({
      next: (gerente) => {
        this.toastService.success('Gerente removido com sucesso');
        this.dataGrid.fetch(false);
      },
      error: err => {
        this.toastService.error(err.error?.error || 'Algo deu errado');
      }
    })
  }

  openDelete(c: Gerente) {
    this.exibirModalExcluir = true;
    this.gerente = c;
  }

  openAdd() {
    this.exibirModalAdicionar = true;
  }

  openEdit(c: Gerente) {
    this.exibirModalEditar = true;
    this.gerente = c;
  }

  closeCallback() {
    this.gerente = undefined;
    this.exibirModalEditar = false;
    this.exibirModalExcluir = false;
    this.exibirModalAdicionar = false;
  }

  deleteCallback() {
    this.dataGrid.fetch(false);
    this.toastService.success('Gerente excluído com sucesso!');
  }

  editCallback() {
    this.dataGrid.fetch(false);
    this.toastService.success('Gerente atualizado com sucesso!');
  }

  carregarGerentes(): Promise<GerenteDashboardResponse[]> {
      return this.loadService.withLoading(() =>
        firstValueFrom(this.gerenteService.getGerentesDashboard()).catch((httpError) => {
          this.toastService.error(httpError.error?.error || 'Algo deu errado');
          throw httpError;
        }),
      );
    }

}
