import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ToastService } from '../../../shared/service/toast/toast';
import { Modal } from "../../../components/modal/modal";
import { NgxMaskDirective } from 'ngx-mask';

@Component({
  selector: 'modal-alterar-perfil',
  imports: [Modal, ReactiveFormsModule, NgxMaskDirective],
  templateUrl: './modal-alterar-perfil.html'
})
export class ModalAlterarPerfil {
  private fb = inject(FormBuilder);
  private toastr = inject(ToastService);

  @Input({ required: true }) control!: boolean;
  @Output() close = new EventEmitter();

  form = this.fb.group({
    nome: ['', [Validators.required], Validators.minLength(10)],
    telefone: ['', [Validators.required], Validators.minLength(10)],
    email: ['', [Validators.required, Validators.email]],
    endereco: ['', [Validators.required]],
    cep: ['', [Validators.required]],
    cidade: ['', [Validators.required]],
    estado: ['', [Validators.required]],
    salario: ['', [Validators.required]],
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
