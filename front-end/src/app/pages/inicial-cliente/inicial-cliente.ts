import { Component } from '@angular/core';
import { Extrato } from "./extrato/extrato";
import { ModalDepositarSacar } from "./modal-depositar-sacar/modal-depositar-sacar";
import { ModalTransferir } from "./modal-transferir/modal-transferir";
import { CurrencyPipe } from '@angular/common';

@Component({
  selector: 'app-inicial-cliente',
  imports: [Extrato, ModalDepositarSacar, ModalTransferir, CurrencyPipe],
  templateUrl: './inicial-cliente.html'
})
export class InicialCliente {

  modalDepositarControl = false;
  modalSacarControl = false;
  modalTransferirControl = false;
  modalAlterarPerfilControl = false;

  currentAccount = {
    name: 'Victor Hugo',
    balance: 43737554357
  }

}
