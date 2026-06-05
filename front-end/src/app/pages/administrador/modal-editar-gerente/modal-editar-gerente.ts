import { ChangeDetectorRef, Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { Modal } from '../../../components/modal/modal';
import { ToastService } from '../../../shared/service/toast/toast';
import { Gerente } from '../../../shared/models/gerente.model';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms'; // Garanta o ReactiveFormsModule aqui se necessário
import { Router } from '@angular/router';
import { LoadingService } from '../../../shared/service/loading.service';
import { EditarGerenteDTO } from '../../../shared/models/resquest/editgerente.model';
import { NgxMaskDirective } from 'ngx-mask';

@Component({
  selector: 'app-modal-editar-gerente',
  standalone: true,
  imports: [Modal, ReactiveFormsModule, NgxMaskDirective],
  templateUrl: './modal-editar-gerente.html'
})
export class ModalEditarGerente {

  private toastr = inject(ToastService);
  private fb = inject(FormBuilder);
  private cdr = inject(ChangeDetectorRef);

  @Input({ required: true }) control!: boolean;
  @Output() close = new EventEmitter<void>();
  @Output() submited = new EventEmitter<EditarGerenteDTO>();

  private _gerente!: Gerente;

  @Input({ required: true }) 
  set gerente(value: Gerente) {
    this._gerente = value;
    if (value) {
      this.atualizarFormulario(value);
    }
  }

  get gerente(): Gerente {
    return this._gerente;
  }

  formCadastro: FormGroup;

  constructor() {
    this.formCadastro = this.fb.group({
      nome: ['', [Validators.required, Validators.minLength(5)]],
      email: ['', [Validators.required, Validators.email]],
      telefone: ['', [Validators.required, Validators.minLength(10)]],
      senha: ['']
    });
  }

  atualizarFormulario(gerente: Gerente) {
    this.formCadastro.patchValue({
      nome: gerente.nome,
      email: gerente.email,
      telefone: gerente.telefone,
      senha: '' 
    });
    this.cdr.detectChanges();
  }

  submit = () => {
    if (this.formCadastro.invalid) {
      this.toastr.error('Por favor, preencha o formulário corretamente.');
      return;
    }
    
    this.submited.emit(this.formCadastro.value as EditarGerenteDTO);
    this.onClose();
  }

  onClose() {
    this.close.emit();
  }
}