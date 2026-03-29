import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { Modal } from '../../../components/modal/modal';
import { ToastService } from '../../../shared/service/toast/toast';

@Component({
  selector: 'app-modal-editar-gerente',
  standalone: true,
  imports: [Modal],
  templateUrl: './modal-editar-gerente.html'
})
export class ModalEditarGerente {

  private toastr = inject(ToastService)
  @Input({ required: true }) control!: boolean;
  @Output() close = new EventEmitter();

  submit = () => {
    this.toastr.success('Gerente atualizado com sucesso!')
    this.onClose()
  }

  onClose() {
    this.close.emit()
  }

}
