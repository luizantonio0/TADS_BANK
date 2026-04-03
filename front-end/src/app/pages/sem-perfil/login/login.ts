import { Component, inject } from '@angular/core';
import { AuthService } from '../../../shared/service/requests/auth';
import { LoginRequestModel} from '../../../shared/models/BarrelFile';
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
  imports: [FormsModule, ReactiveFormsModule],
  templateUrl: './login.html',
})
export class Login {
  authService = inject(AuthService);
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
      await this.router.navigate(['/cliente']);
    }
  }

  send_login_request(login_model: LoginRequestModel) {
    this.authService.login(login_model).subscribe({
      next: (response) => {
        console.log(response);
      },
      error: (error) => {
        console.log(error);
      },
    });
  }
}
