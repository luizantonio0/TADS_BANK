import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { Modal } from '../../../components/modal/modal';
import { ToastService } from '../../../shared/service/toast/toast';

@Component({
  selector: 'app-modal-excluir-gerente',
  imports: [Modal],
  templateUrl: './modal-excluir-gerente.html',
  styleUrl: './modal-excluir-gerente.css',
})
export class ModalExcluirGerente {
  private toastr = inject(ToastService)
  @Input({ required: true }) control!: boolean;
  @Output() close = new EventEmitter();

  submit = () => {
    this.toastr.success('Gerente excluído com sucesso!')
    this.onClose()
  }

  onClose() {
    this.close.emit()
  }
}
