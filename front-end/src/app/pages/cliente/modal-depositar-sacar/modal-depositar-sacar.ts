import { Component, EventEmitter, inject, Input, ChangeDetectorRef, Output } from '@angular/core';
import { Modal } from '../../../components/modal/modal';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { NgxMaskDirective } from 'ngx-mask';
import { ToastService } from '../../../shared/service/toast/toast';
import { ContaService } from '../../../shared/service/requests/conta';

@Component({
  selector: 'modal-depositar-sacar',
  imports: [Modal, NgxMaskDirective, ReactiveFormsModule],
  templateUrl: './modal-depositar-sacar.html'
})
export class ModalDepositarSacar {

  private fb = inject(FormBuilder);
  private toastr = inject(ToastService);
  private cdr = inject(ChangeDetectorRef)
  private contaService = inject(ContaService)


  @Input({ required: true }) control!: boolean;
  @Input({ required: true }) tipo!: 'deposit' | 'withdraw';
  @Input() numeroConta!: string;

  @Output() close = new EventEmitter();

  form = this.fb.group({
    amount: ['', [Validators.required]],
  });

  submit = () => {
    if (this.form.valid) {
      const valor = Number(this.form.value.amount);

      if (this.tipo === 'deposit') {
        this.contaService.depositar(this.numeroConta, valor).subscribe({
          next: (res) => {
            this.toastr.success(`Depósito de R$ ${res.valor} realizado com sucesso!`);
            this.form.reset();
            this.onClose();
            this.cdr.detectChanges();
          },
          error: (err) => {
            this.toastr.error('Erro ao realizar depósito');
            console.error(err);
          }
        });
      } else {
        this.toastr.success('Saque realizado com sucesso')
        this.onClose()
      }
    }
  }

  onClose() {
    this.close.emit();
  }
}
