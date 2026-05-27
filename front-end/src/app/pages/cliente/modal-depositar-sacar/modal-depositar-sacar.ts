import { Component, EventEmitter, inject, Input, ChangeDetectorRef, Output } from '@angular/core';
import { Modal } from '../../../components/modal/modal';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { NgxMaskDirective } from 'ngx-mask';
import { ToastService } from '../../../shared/service/toast/toast';
import { ContaService } from '../../../shared/service/requests/conta.service';

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

  @Input({ required: true }) callback!: () => void;
  @Input({ required: true }) control!: boolean;
  @Input({ required: true }) tipo!: 'DEPOSITO' | 'SAQUE';
  @Input() numeroConta!: string;

  @Output() close = new EventEmitter();

  form = this.fb.group({
    amount: ['', [Validators.required]],
  });

  private parseValorMonetario(valor: unknown): number {
    if (typeof valor === 'number') {
      return valor;
    }

    if (typeof valor === 'string') {
      const normalizado = valor.replace(/\./g, '').replace(',', '.').trim();
      return Number(normalizado);
    }

    return Number.NaN;
  }

  submit = () => {
    if (this.form.valid) {
      const valor = this.parseValorMonetario(this.form.value.amount);

      if (!Number.isFinite(valor) || valor <= 0) {
        this.toastr.error('Informe um valor válido para a operação');
        return;
      }

      if (this.tipo === 'DEPOSITO') {
        this.contaService.depositar(this.numeroConta, valor).subscribe({
          next: (res) => {
            this.toastr.success(`Depósito de R$ ${valor} realizado com sucesso!`);
            this.form.reset();
            this.callback();
            this.onClose();
            this.cdr.detectChanges();
          },
          error: (err) => {
            this.toastr.error('Erro ao realizar depósito');
            console.error(err);
          }
        });
      } else {
        this.contaService.sacar(this.numeroConta, valor).subscribe({
          next: (res) => {
            this.toastr.success(`Saque de R$ ${valor} realizado com sucesso!`);
            this.form.reset();
            this.callback();
            this.onClose();
            this.cdr.detectChanges();
          },
          error: (err) => {
            this.toastr.error("Erro ao realizar saque");
            console.error(err)
          }
        });
      }
    }
  }

  onClose() {
    this.close.emit();
  }
}
