import { ChangeDetectorRef, Component, inject, Input, OnInit } from '@angular/core';
import { Extrato } from "./extrato/extrato";
import { ModalDepositarSacar } from "./modal-depositar-sacar/modal-depositar-sacar";
import { ModalTransferir } from "./modal-transferir/modal-transferir";
import { CurrencyPipe } from '@angular/common';
import { ModalAlterarPerfil } from "./modal-alterar-perfil/modal-alterar-perfil";
import { Cliente } from '../../shared/models/cliente.model';
import { ClienteService } from '../../shared/service/requests/cliente.service';
import { LoadingService } from '../../shared/service/loading.service';
import { ToastService } from '../../shared/service/toast/toast';

@Component({
  selector: 'app-inicial-cliente',
  imports: [Extrato, ModalDepositarSacar, ModalTransferir, CurrencyPipe, ModalAlterarPerfil],
  templateUrl: './inicial-cliente.html',
})
export class InicialCliente implements OnInit {
  private clienteService = inject(ClienteService);
  private loadingService = inject(LoadingService);
  private toastService = inject(ToastService);
  private cdRef = inject(ChangeDetectorRef);

  @Input() cliente?: Cliente;

  modalDepositarControl = false;
  modalSacarControl = false;
  modalTransferirControl = false;
  modalAlterarPerfilControl = false;
  isLoadingCliente = true;
  clienteCarregado = false;

  currentAccount: CurrentAccount = {
    name: '',
    balance: 0,
    limite: 0,
    gerente: '',
    email: '',
    numeroConta: ''
  };

  ngOnInit() {
    const clienteState = history.state.cliente as Cliente | undefined;
    if (clienteState) {
      this.setCliente(clienteState);
    }

    this.carregarClienteLogado();
  }

  carregarClienteLogado() {
    this.isLoadingCliente = true;
    const usuario = this.getUsuarioLogado();

    if (!usuario?.cpf) {
      this.isLoadingCliente = false;
      this.clienteCarregado = false;
      this.toastService.error('Nao foi possivel identificar o cliente logado.');
      return;
    }

    this.loadingService.withLoadingObservable(this.clienteService.getCliente(usuario.cpf)).subscribe({
      next: (cliente) => this.setCliente(cliente),
      error: (error) => {
        this.isLoadingCliente = false;
        this.clienteCarregado = false;
        this.toastService.error(error.error?.error || 'Erro ao carregar dados do cliente.');
        this.cdRef.detectChanges();
      }
    });
  }

  fecharModalOperacao() {
    this.modalSacarControl = false;
    this.modalDepositarControl = false;
    this.modalTransferirControl = false;
    this.carregarClienteLogado();
  }

  fecharModalPerfil() {
    this.modalAlterarPerfilControl = false;
    this.carregarClienteLogado();
  }

  private setCliente(cliente: Cliente) {
    this.cliente = cliente;
    this.currentAccount = {
      name: this.cliente.nome,
      balance: this.cliente.saldo ?? 0,
      limite: this.cliente.limite ?? 0,
      gerente: this.cliente.gerente_nome ?? '',
      email: this.cliente.gerente_email ?? '',
      numeroConta: this.cliente.conta? String(this.cliente.conta) : ''
    };
    this.isLoadingCliente = false;
    this.clienteCarregado = true;
    this.cdRef.detectChanges();
  }

  private getUsuarioLogado(): UsuarioLogado | null {
    const usuario = sessionStorage.getItem('usuario');

    if (!usuario) {
      return null;
    }

    try {
      return JSON.parse(usuario);
    } catch {
      return null;
    }
  }
}

interface UsuarioLogado {
  cpf: string;
}

interface CurrentAccount {
  name: string,
  balance: number,
  limite: number,
  gerente: string,
  email: string,
  numeroConta: string
}
