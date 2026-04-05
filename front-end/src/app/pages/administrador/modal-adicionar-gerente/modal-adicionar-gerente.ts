import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { Modal } from '../../../components/modal/modal';
import { ToastService } from '../../../shared/service/toast/toast';

@Component({
  selector: 'app-modal-adicionar-gerente',
  imports: [Modal],
  templateUrl: './modal-adicionar-gerente.html',
  styleUrl: './modal-adicionar-gerente.css',
})
export class ModalAdicionarGerente {

  private toastr = inject(ToastService)
  @Input({ required: true }) control!: boolean;
  @Output() close = new EventEmitter();

  submit = () => {
    this.toastr.success('Gerente adicionado com sucesso!')
    this.onClose()
  }

  onClose() {
    this.close.emit()
  }
}
