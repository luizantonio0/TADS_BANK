import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { Modal } from "../../../components/modal/modal";
import { ToastService } from '../../../shared/service/toast/toast';
import { FormsModule } from "@angular/forms";
import type { Cliente } from '../../../shared/models/cliente.model';

@Component({
  selector: 'modal-rejeitar-cliente',
  imports: [Modal, FormsModule],
  templateUrl: './modal-rejeitar-cliente.html'
})
export class ModalRejeitarCliente {
  motivo = '';

  @Input({ required: true }) control!: boolean;
  @Output() close = new EventEmitter();
  @Output() submited = new EventEmitter<string>();

  constructor(private toast: ToastService) {
  }

  submit = () => {
    if(this.motivo.length < 5) {
      console.log(this.motivo)
      this.toast.error("Especifique o motivo da rejeição.");
      return;
    }
    this.submited.emit(this.motivo);
    this.onClose();
  }

  onClose() {
    this.close.emit();
  }

}
