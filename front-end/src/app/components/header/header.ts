import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { ModalConfirmarLogout } from '../../pages/sem-perfil/modal-confirmar-logout/modal-confirmar-logout';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterModule, ModalConfirmarLogout],
  templateUrl: './header.html',
  styleUrls: ['./header.css'],
})
export class Header {
  @Input() isAdmin: boolean = false;
  @Input() isGerente: boolean = false;
  @Input() isCliente: boolean = false;
  @Input() isPublic: boolean = false; 

  exibirModalLogout: boolean = false;

  constructor(private router: Router) {}


  abrirModalLogout() {
    this.exibirModalLogout = true;
  }

  logout(): void {
    localStorage.removeItem('usuarioLogado');
    this.router.navigate(['/login']);
  }
}
