import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { Modal } from '../../../components/modal/modal';
import { ToastService } from '../../../shared/service/toast/toast';

@Component({
  selector: 'app-modal-confirmar-logout',
  imports: [Modal],
  templateUrl: './modal-confirmar-logout.html',
  styleUrl: './modal-confirmar-logout.css',
})
export class ModalConfirmarLogout {
  private toastr = inject(ToastService)
  @Input({ required: true }) control!: boolean;
  @Output() close = new EventEmitter();
  @Output() confirm = new EventEmitter();

  submit = () => {
    this.confirm.emit()
    this.onClose()
  }

  onClose() {
    this.close.emit()
  }
}
