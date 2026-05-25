import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { DataGridComponent } from "../../../components/data-grid/data-grid";
import { DataGridColumns } from '../../../shared/models/datagrid.model';
import { LoadingService } from '../../../shared/service/loading.service';
import { ToastService } from '../../../shared/service/toast/toast';
import { GerenteService } from '../../../shared/service/requests/gerente.service';
import { GerenteDashboardResponse } from '../../../shared/models/gerente.model';
import { firstValueFrom } from 'rxjs';
import { CurrencyPipe, NgClass } from '@angular/common';
import { CpfPipe } from '../../../shared/pipe/cpf.pipe';

@Component({
  selector: 'tab-dashboard-admin',
  imports: [DataGridComponent, CurrencyPipe, CpfPipe, NgClass],
  templateUrl: './tab-dashboard-admin.html',
  styleUrl: './tab-dashboard-admin.css',
})

export class TabDashboardAdmin implements OnInit {

  columns: DataGridColumns[] = [
    { size: 20, title: 'Nome' },
    { size: 15, title: 'CPF' },
    { size: 15, title: 'Total de clientes' },
    { size: 25, title: 'Saldos positivos (+)' },
    { size: 25, title: 'Saldos negativos (-)' }
  ]

  gerentes: GerenteDashboardResponse[] = [];

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

  carregarGerentes(): Promise<GerenteDashboardResponse[]> {
      return this.loadService.withLoading(() =>
        firstValueFrom(this.gerenteService.getGerentesDashboard()).catch((httpError) => {
          this.toastService.error(httpError.error?.error || 'Algo deu errado');
          throw httpError;
        }),
      );
    }

}
