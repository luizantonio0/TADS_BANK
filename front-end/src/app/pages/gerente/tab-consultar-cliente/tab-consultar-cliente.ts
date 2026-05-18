import { ChangeDetectorRef, Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgxMaskDirective } from 'ngx-mask';
import { Cliente } from '../../../shared/models/cliente.model';
import { CpfPipe } from '../../../shared/pipe/cpf.pipe';
import { CurrencyPipe } from '@angular/common';
import { TelefonePipe } from '../../../shared/pipe/telefone.pipe';
import { ClienteService } from '../../../shared/service/requests/cliente.service';
import { finalize } from 'rxjs';

@Component({
  selector: 'tab-consultar-cliente',
  imports: [NgxMaskDirective, FormsModule, CpfPipe, CurrencyPipe, TelefonePipe],
  templateUrl: './tab-consultar-cliente.html'
})
export class TabConsultarCliente {
  private clienteService = inject(ClienteService);
  private cdr = inject(ChangeDetectorRef);

  cpf = "";
  cliente: Cliente | null = null;
  erro = "";
  carregando = false;

  submit() {
    if (this.cpf.length !== 11) {
      this.cliente = null;
      this.erro = "Informe um CPF válido.";
      return;
    }

    this.carregando = true;
    this.erro = "";
    this.cliente = null;

    this.clienteService.getCliente(this.cpf)
      .pipe(finalize(() => {
        this.carregando = false;
        this.cdr.detectChanges();
      }))
      .subscribe({
      next: (cliente) => {
        this.cliente = cliente;
        this.cdr.detectChanges();
      },
      error: () => {
        this.erro = "Cliente não encontrado.";
        this.cdr.detectChanges();
      },
    });
  }
}
