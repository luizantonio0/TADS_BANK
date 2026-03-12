import { Component } from '@angular/core';
import { Header } from "../../components/header/header";
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  imports: [Header, FormsModule, ReactiveFormsModule],
  templateUrl: './login.html'
})
export class Login {

  formLogin: FormGroup;

  constructor(private router: Router, private fb: FormBuilder) {
    this.formLogin = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      senha: ['', [Validators.required]],
    });
  }

  async submit() {
    if (this.formLogin.valid) {
      await this.router.navigate(['/cliente']);
    }
  }

}
