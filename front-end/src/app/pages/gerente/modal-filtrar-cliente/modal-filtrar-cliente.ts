import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { Modal } from "../../../components/modal/modal";
import { ToastService } from '../../../shared/service/toast/toast';
import { FormsModule } from "@angular/forms";

@Component({
  selector: 'modal-filtrar-cliente',
  imports: [Modal, FormsModule],
  templateUrl: './modal-filtrar-cliente.html'
})
export class ModalFiltrarCliente {
  toastr = inject(ToastService);

  @Input({ required: true }) control!: boolean;
  @Output() close = new EventEmitter();
  @Output() filtered = new EventEmitter<string>();

  valor = '';

  submit = () => {
    this.toastr.success('Filtros aplicados com sucesso!');
    this.filtered.emit(this.valor);
    this.onClose();
  }

  onClose() {
    this.close.emit();
  }

}
