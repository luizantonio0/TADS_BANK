import { Component, inject } from '@angular/core';
import { Cliente } from '../../../shared/models/BarrelFile';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';
import { NgxMaskDirective } from 'ngx-mask';
import { ClienteService } from '../../../shared/service/requests/cliente.service';
import { ToastService } from '../../../shared/service/toast/toast';
import { cpfValidator } from '../../../shared/validators/cpf.validator';
import { CEPService } from '../../../shared/service/cep.service';
import { LoadingService } from '../../../shared/service/loading.service';
@Component({
  selector: 'app-autocadastro',
  imports: [ReactiveFormsModule, FormsModule, NgxMaskDirective],
  templateUrl: './autocadastro.html',
})
export class Autocadastro {
  clienteService = inject(ClienteService);
  cepService = inject(CEPService);
  private toastr = inject(ToastService);
  formCadastro: FormGroup;

  constructor(
    private router: Router,
    private fb: FormBuilder,
    private loadingService: LoadingService,
    private toastService: ToastService
  ) {
    this.formCadastro = this.fb.group({
      cpf: ['', [Validators.required, cpfValidator()]],
      nome: ['', [Validators.required, Validators.minLength(10)]],
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
  }

  async submit() {
    if (this.formCadastro.valid) {
      this.autocadastrar(this.formCadastro.value);
    }
  }

  onlyNumbers(event: KeyboardEvent) {
    const allowedKeys = ['Backspace', 'Delete', 'ArrowLeft', 'ArrowRight', 'Tab'];
    if (!allowedKeys.includes(event.key) && isNaN(Number(event.key))) {
      event.preventDefault();
    }
  }

  async buscarCep() {
    const cepAtual = this.formCadastro.get('cep')?.value;
    if (!cepAtual || cepAtual.replaceAll("\\D", "").length != 8) {
      this.toastr.error('Digite um CEP válido!');
      return;
    }
    this.loadingService.withLoadingObservable(this.cepService.buscarCEP(cepAtual)).subscribe({
      next: (endereco) => {
        if (endereco.erro) {
          this.toastr.error('Digite um CEP válido.');
          return;
        }
        this.formCadastro.patchValue({
          cidade: endereco.localidade,
          estado: endereco.uf,
          endereco: endereco.logradouro + ", " + endereco.bairro,
          bairro: endereco.bairro,
          logradouro: endereco.logradouro
        })
      }
    })
  }

  autocadastrar(val: any) {
    this.loadingService.withLoadingObservable(this.clienteService.autoCadastrar({
      cpf: val.cpf,
      cidade: val.cidade,
      email: val.email,
      endereco: `${val.logradouro}, ${val.numero}, ${val.bairro}`,
      estado: val.estado,
      nome: val.nome,
      salario: val.salario,
      telefone: val.telefone,
      CEP: val.cep
    })).subscribe({
      next: (response) => {
        this.toastr.success('Um de nossos gerentes recebeu sua aplicação. Fique atento ao seu e-mail.');
        this.formCadastro.reset();
      },
      error: (error) => {
        this.toastService.error(error.error?.error || "Algo deu errado");
      },
    });
  }
}
