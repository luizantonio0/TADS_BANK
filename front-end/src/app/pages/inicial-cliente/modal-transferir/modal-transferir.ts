import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { Modal } from "../../../components/modal/modal";
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ToastService } from '../../../shared/service/toast/toast';
import { NgxMaskDirective } from 'ngx-mask';

@Component({
  selector: 'modal-transferir',
  imports: [Modal, ReactiveFormsModule, NgxMaskDirective],
  templateUrl: './modal-transferir.html'
})
export class ModalTransferir {
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
        this.toastr.success('Transferencia realizada com sucesso!');
        this.onClose();
    }
  }

  onClose() {
    this.close.emit();
  }
}
