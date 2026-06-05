import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { Modal } from '../../../components/modal/modal';
import { ToastService } from '../../../shared/service/toast/toast';
import { GerenteService } from '../../../shared/service/requests/gerente.service';
import { LoadingService } from '../../../shared/service/loading.service';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { cpfValidator } from '../../../shared/validators/cpf.validator';
import { Gerente } from '../../../shared/models/gerente.model';
import { CadastroGerenteDTO } from '../../../shared/models/resquest/cadastrogerente.model';
import { NgxMaskDirective } from 'ngx-mask';

@Component({
  selector: 'app-modal-adicionar-gerente',
  imports: [Modal, FormsModule, ReactiveFormsModule, NgxMaskDirective],
  templateUrl: './modal-adicionar-gerente.html',
  styleUrl: './modal-adicionar-gerente.css',
})
export class ModalAdicionarGerente {
  private toastr = inject(ToastService);
  private gerenteService = inject(GerenteService);
  private loadService = inject(LoadingService);
  formCadastro: FormGroup;

  @Input({ required: true }) control!: boolean;
  @Output() close = new EventEmitter();
  @Output() submited = new EventEmitter<CadastroGerenteDTO>();

  constructor(private fb: FormBuilder) {
    this.formCadastro = this.fb.group({
      cpf: ['', [Validators.required, cpfValidator()]],
      nome: ['', [Validators.required, Validators.minLength(10)]],
      email: ['', [Validators.required, Validators.email]],
      telefone: ['', [Validators.required, Validators.minLength(10)]],
      tipo: ['GERENTE', [Validators.required]],
      senha: ['', [Validators.required]],
    });
  }

  submit = () => {
    this.submited.emit(this.formCadastro.value as CadastroGerenteDTO);
    this.onClose();
  };

  onClose() {
    this.close.emit();
  }
}
