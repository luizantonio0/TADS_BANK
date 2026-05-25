import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { ModalConfirmarLogout } from '../../pages/sem-perfil/modal-confirmar-logout/modal-confirmar-logout';
import { AuthService } from '../../shared/service/requests/auth.service';
import { LoadingService } from '../../shared/service/loading.service';
import { ToastService } from '../../shared/service/toast/toast';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterModule, ModalConfirmarLogout],
  templateUrl: './header.html'
})
export class Header {
  @Input() isAdmin: boolean = false;
  @Input() isGerente: boolean = false;
  @Input() isCliente: boolean = false;
  @Input() isPublic: boolean = false;

  exibirModalLogout: boolean = false;

  constructor(private router: Router, private authService: AuthService, private loadService: LoadingService, private toastService: ToastService) {}


  abrirModalLogout() {
    this.exibirModalLogout = true;
  }

  logout(): void {
    this.loadService.withLoadingObservable(this.authService.logout()).subscribe({
      next: _ => {
        sessionStorage.clear();
        this.router.navigate(['/login']);
      },
      error: err => {
        this.toastService.error(err.error?.error || 'Algo deu errado');
      }
    });
  }
}
