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

  //todo: não consegui testar o submit com o FormGroup e as validações ativas, esta funcionando sem os Validators
  constructor(
    private router: Router,
    private fb: FormBuilder,
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
      numero: ['', [Validators.required]]
    });
  }

  async submit() {
    if (this.formCadastro.valid) {
      this.autocadastrar(this.formCadastro.value);
      this.toastr.success('Cadastro realizado com sucesso!');
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
    if(!cepAtual || cepAtual.replaceAll("\\D", "").length != 8) {
      this.toastr.error('Digite um CEP válido!');
      return;
    }
    debugger
    this.cepService.buscarCEP(cepAtual).subscribe({
      next: (endereco) => {
        debugger
        this.formCadastro.patchValue({
          cidade: endereco.localidade,
          estado: endereco.uf,
          endereco: endereco.logradouro + ", " + endereco.bairro
        })
      },
      error: (err) => {
        this.toastr.error('Algo deu errado!');
      }
    })
  }

  autocadastrar(val: any) {
    this.clienteService.autoCadastrar({
      cpf: val.cpf,
      cidade: val.cidade,
      email: val.email,
      endereco: val.ender

    }).subscribe({
      next: (response) => {
        console.log('Cadastro realizado com sucesso', response);
      },
      error: (error) => {
        console.error('Erro ao cadastrar cliente', error);
      },
    });
  }
}
