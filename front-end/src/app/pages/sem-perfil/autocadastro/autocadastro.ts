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
import { ClienteService } from '../../../shared/service/requests/cliente';
import { ToastService } from '../../../shared/service/toast/toast';
import { cpfValidator } from '../../../shared/validators/cpf.validator';
@Component({
  selector: 'app-autocadastro',
  imports: [ReactiveFormsModule, FormsModule, NgxMaskDirective],
  templateUrl: './autocadastro.html',
})
export class Autocadastro {
  clienteService = inject(ClienteService);
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
    });
  }

  async submit() {
    if (this.formCadastro.valid) {
      this.autocadastrar(this.formCadastro.value);
      this.toastr.success('Cadastro realizado com sucesso!');
    }
  }

  autocadastrar(cliente: Cliente) {
    this.clienteService.autoCadastrar(cliente).subscribe({
      next: (response) => {
        console.log('Cadastro realizado com sucesso', response);
      },
      error: (error) => {
        console.error('Erro ao cadastrar cliente', error);
      },
    });
  }
}
