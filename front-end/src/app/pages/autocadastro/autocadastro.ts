import { Component } from '@angular/core';
import { Header } from '../../components/header/header';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';
import { NgxMaskDirective } from 'ngx-mask';

@Component({
  selector: 'app-autocadastro',
  imports: [ReactiveFormsModule, FormsModule, NgxMaskDirective],
  templateUrl: './autocadastro.html',
})
export class Autocadastro {

  formCadastro: FormGroup;

  constructor(
    private router: Router,
    private fb: FormBuilder,
  ) {
    this.formCadastro = this.fb.group({
      cpf: ['', [Validators.required, Validators.email]],
      nome: ['', [Validators.required], Validators.minLength(10)],
      telefone: ['', [Validators.required], Validators.minLength(10)],
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
      await this.router.navigate(['/cliente']);
    }
  }

}
