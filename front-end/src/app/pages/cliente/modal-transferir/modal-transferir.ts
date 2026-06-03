import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { Modal } from '../../../components/modal/modal';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ToastService } from '../../../shared/service/toast/toast';
import { NgxMaskDirective } from 'ngx-mask';
import { ContaService } from '../../../shared/service/requests/conta.service';

@Component({
  selector: 'modal-transferir',
  imports: [Modal, ReactiveFormsModule, NgxMaskDirective],
  templateUrl: './modal-transferir.html',
})
export class ModalTransferir {
  private fb = inject(FormBuilder);
  private toastr = inject(ToastService);
  private contaService = inject(ContaService);

  @Input({ required: true }) callback!: () => void;
  @Input({ required: true }) control!: boolean;
  @Input({ required: true }) numeroConta!: string;
  @Output() close = new EventEmitter();

  form = this.fb.group({
    account: ['', [Validators.required, Validators.maxLength(4)]],
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
      const contaDestino = this.form.value.account!;
      const valor = this.parseValorMonetario(this.form.value.amount);

      if (!Number.isFinite(valor) || valor <= 0) {
        this.toastr.error('Informe um valor válido para a transferência');
        return;
      }

      this.contaService.transferir(this.numeroConta, contaDestino, valor).subscribe({
        next: (res) => {
          this.toastr.success(
            `Transferência de R$ ${valor} para a conta ${contaDestino} realizada com sucesso!`,
          );
          this.callback();
          this.form.reset();
          this.onClose();
        },
        error: (err) => {
          this.toastr.error('Erro ao realizar transferencia');
        },
      });
    }
  };

  onClose() {
    this.close.emit();
  }
}
