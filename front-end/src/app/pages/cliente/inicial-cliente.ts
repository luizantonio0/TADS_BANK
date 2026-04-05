import { Component, Input, OnInit } from '@angular/core';
import { Extrato } from "./extrato/extrato";
import { ModalDepositarSacar } from "./modal-depositar-sacar/modal-depositar-sacar";
import { ModalTransferir } from "./modal-transferir/modal-transferir";
import { CurrencyPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ModalAlterarPerfil } from "./modal-alterar-perfil/modal-alterar-perfil";
import { Cliente } from '../../shared/models/cliente.model';

@Component({
  selector: 'app-inicial-cliente',
  imports: [Extrato, ModalDepositarSacar, ModalTransferir, CurrencyPipe, ModalAlterarPerfil],
  templateUrl: './inicial-cliente.html',
})
export class InicialCliente implements OnInit {
  @Input() cliente?: Cliente;

  modalDepositarControl = false;
  modalSacarControl = false;
  modalTransferirControl = false;
  modalAlterarPerfilControl = false;

  currentAccount: CurrentAccount = {
    name: '',
    balance: 0,
    limite: 0,
    gerente: '',
    email: '',
  };

  ngOnInit() {
    this.cliente = history.state.cliente

    if (!this.cliente) return


    this.currentAccount = {
      name: this.cliente.nome,
      balance: this.cliente.saldo? this.cliente.saldo: 0,
      limite: this.cliente.limite? this.cliente.limite: 0,
      gerente: this.cliente.gerente_nome? this.cliente.gerente_nome: '',
      email: this.cliente.gerente_email? this.cliente.gerente_email: ''
    }
  }
}

interface CurrentAccount {
  name: string,
  balance: number,
  limite: number,
  gerente: string,
  email: string,
}
