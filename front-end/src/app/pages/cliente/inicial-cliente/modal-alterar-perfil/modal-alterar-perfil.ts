import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ToastService } from '../../../../shared/service/toast/toast';
import { Modal } from "../../../../components/modal/modal";

@Component({
  selector: 'modal-alterar-perfil',
  imports: [Modal],
  templateUrl: './modal-alterar-perfil.html'
})
export class ModalAlterarPerfil {
  private fb = inject(FormBuilder);
  private toastr = inject(ToastService);

  @Input({ required: true }) control!: boolean;

  @Output() close = new EventEmitter();

  form = this.fb.group({
    account: ['', [Validators.required, Validators.maxLength(4)]],
    amount: ['', [Validators.required]],
  });

  submit = () => {
    if (this.form.valid) {
        this.toastr.success('Alterações salvas com sucesso!');
        this.onClose();
    }
  }

  onClose() {
    this.close.emit();
  }
}
