import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { Modal } from '../../../components/modal/modal';
import { ToastService } from '../../../shared/service/toast/toast';
import { GerenteService } from '../../../shared/service/requests/gerente.service';
import { LoadingService } from '../../../shared/service/loading.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { cpfValidator } from '../../../shared/validators/cpf.validator';
import { Gerente } from '../../../shared/models/gerente.model';

@Component({
  selector: 'app-modal-adicionar-gerente',
  imports: [Modal],
  templateUrl: './modal-adicionar-gerente.html',
  styleUrl: './modal-adicionar-gerente.css',
})
export class ModalAdicionarGerente {
  private toastr = inject(ToastService);
  private gerenteService = inject(GerenteService);
  private loadService = inject(LoadingService);
  private formCadastro: FormGroup;

  @Input({ required: true }) control!: boolean;
  @Output() close = new EventEmitter();
  @Output() submited = new EventEmitter<Gerente>();

  constructor(private fb: FormBuilder) {
    this.formCadastro = this.fb.group({
      cpf: ['', [Validators.required, cpfValidator()]],
      nome: ['', [Validators.required, Validators.minLength(10)]],
      email: ['', [Validators.required, Validators.email]],
      tipo: ['', [Validators.required, Validators.email]],
      senha: ['', [Validators.required]],
    });
  }

  submit = () => {
    this.submited.emit(this.formCadastro.value);
    this.onClose();
  };

  onClose() {
    this.close.emit();
  }
}
