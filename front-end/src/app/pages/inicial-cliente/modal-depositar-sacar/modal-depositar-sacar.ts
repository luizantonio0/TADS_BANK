import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { Modal } from "../../../components/modal/modal";
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { NgxMaskDirective } from 'ngx-mask';
import { ToastService } from '../../../shared/service/toast/toast';

@Component({
  selector: 'modal-depositar-sacar',
  imports: [Modal, NgxMaskDirective, ReactiveFormsModule],
  templateUrl: './modal-depositar-sacar.html'
})
export class ModalDepositarSacar {

  private fb = inject(FormBuilder);
  private toastr = inject(ToastService);

  @Input({ required: true }) control!: boolean;
  @Input({ required: true }) tipo!: 'deposit' | 'withdraw';

  @Output() close = new EventEmitter();

  form = this.fb.group({
    amount: ['', [Validators.required]],
  });

  submit = () => {
    if (this.form.valid) {
      if (this.tipo === 'deposit') {
        this.toastr.success('Depósito realizado com sucesso!');
        this.onClose();
      } else {
        this.toastr.success('Saque realizado com sucesso!');
        this.onClose();
      }
    }
  }

  onClose() {
    this.close.emit();
  }

}
