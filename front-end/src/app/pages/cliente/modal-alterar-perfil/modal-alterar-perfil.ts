import { Component, EventEmitter, inject, Input, Output, OnChanges } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ToastService } from '../../../shared/service/toast/toast';
import { Modal } from "../../../components/modal/modal";
import { NgxMaskDirective } from 'ngx-mask';
import { ClienteService } from '../../../shared/service/requests/cliente.service';
import { Cliente } from '../../../shared/models/cliente.model';
import { CEPService } from '../../../shared/service/cep.service';
import { LoadingService } from '../../../shared/service/loading.service';

@Component({
  selector: 'modal-alterar-perfil',
  standalone: true,
  imports: [Modal, ReactiveFormsModule, NgxMaskDirective],
  templateUrl: './modal-alterar-perfil.html',
})
export class ModalAlterarPerfil implements OnChanges {
  private fb = inject(FormBuilder);
  private toastr = inject(ToastService);
  private service = inject(ClienteService);
  private cepService = inject(CEPService);
  private loadingService = inject(LoadingService);

  @Input({ required: true }) control!: boolean;
  @Input() cliente?: Cliente;
  @Output() close = new EventEmitter();

  form = this.fb.group({
    nome: ['', [Validators.required, Validators.minLength(5)]],
    telefone: ['', [Validators.required, Validators.minLength(10)]],
    email: ['', [Validators.required, Validators.email]],
    endereco: ['', [Validators.required]],
    cep: ['', [Validators.required]],
    cidade: ['', [Validators.required]],
    estado: ['', [Validators.required]],
    salario: ['', [Validators.required]],
    numero: ['', [Validators.required]],
    bairro: [''],
    logradouro: ['']
  });

  ngOnChanges() {
    if (this.cliente && this.control) {
      const parts = this.cliente.endereco.split(', ');
      this.form.patchValue({
        nome: this.cliente.nome,
        telefone: this.cliente.telefone,
        email: this.cliente.email,
        endereco: this.cliente.endereco,
        cep: this.cliente.cep,
        cidade: this.cliente.cidade,
        estado: this.cliente.estado,
        salario: this.cliente.salario?.toString(),
        logradouro: parts[0] || '',
        numero: parts[1] || '',
        bairro: parts[2] || ''
      });
    }
  }

  async buscarCep() {
    const cepAtual = this.form.get('cep')?.value;
    if (!cepAtual || cepAtual.toString().length < 8) {
      this.toastr.error('Digite um CEP válido!');
      return;
    }
    this.loadingService.withLoadingObservable(this.cepService.buscarCEP(cepAtual)).subscribe({
      next: (endereco) => {
        if (endereco.erro) {
          this.toastr.error('CEP não encontrado.');
          return;
        }
        this.form.patchValue({
          cidade: endereco.localidade,
          estado: endereco.uf,
          endereco: endereco.logradouro + ", " + endereco.bairro,
          bairro: endereco.bairro,
          logradouro: endereco.logradouro
        })
      }
    })
  }

  onlyNumbers(event: KeyboardEvent) {
    const allowedKeys = ['Backspace', 'Delete', 'ArrowLeft', 'ArrowRight', 'Tab'];
    if (!allowedKeys.includes(event.key) && isNaN(Number(event.key))) {
      event.preventDefault();
    }
  }

  submit = () => {
    if (this.form.invalid) {
      this.toastr.error('Por favor, preencha o formulário corretamente.');
      return;
    }

    if (!this.cliente?.cpf) {
      this.toastr.error('Nao foi possivel identificar o cliente.');
      return;
    }

    const val: any = this.form.value;
    const payload = {
      ...val,
      endereco: `${val.logradouro || val.endereco.split(',')[0]}, ${val.numero}, ${val.bairro || val.endereco.split(',')[1] || ''}`
    };

    this.alterarPerfil(payload);
  };

  alterarPerfil(cliente: any) {
    this.service.alterarCliente(this.cliente!.cpf, cliente).subscribe({
      next: () => {
        this.toastr.success('Alteracoes salvas com sucesso!');
        this.onClose();
      },
      error: (error) => {
        this.toastr.error(error.error?.error || 'Erro ao alterar cliente');
      },
    });
  }

  onClose() {
    this.close.emit();
  }
}
