import { Component, inject } from '@angular/core';
import { AuthService } from '../../../shared/service/requests/auth.service';
import { LoginRequestModel} from '../../../shared/models/BarrelFile';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';
import { ClienteService } from '../../../shared/service/requests/cliente.service';

@Component({
  selector: 'app-login',
  imports: [FormsModule, ReactiveFormsModule],
  templateUrl: './login.html',
})
export class Login {
  authService = inject(AuthService);
  clienteService = inject(ClienteService);
  formLogin: FormGroup;

  constructor(
    private router: Router,
    private fb: FormBuilder,
  ) {
    this.formLogin = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      senha: ['', [Validators.required]],
    });
  }

  async submit() {
    if (this.formLogin.valid) {
      this.send_login_request(this.formLogin.value);
      this.go_to_client_inicial(this.formLogin.value.email);
    }
  }

  go_to_client_inicial(email: string) {
    this.clienteService.getCliente(email).subscribe({
      next: (response) => {
        console.log(response);
        this.router.navigate(['/cliente'], {
          state: { cliente: response },
        });
      },
      error: (error) => {
        console.log(error);
      },
    });
  }

  send_login_request(login_model: LoginRequestModel) {
    this.authService.login(login_model).subscribe({
      next: (response) => {
        console.log(response);
        sessionStorage.setItem('access_token', response.access_token);
      },
      error: (error) => {
        console.log(error);
      },
    });
  }
}
