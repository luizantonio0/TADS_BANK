import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './header.html',
  styleUrls: ['./header.css'],
})
export class Header {
  @Input() isAdmin: boolean = false;
  @Input() isGerente: boolean = false;
  @Input() isCliente: boolean = false;
  @Input() isPublic: boolean = false; 

  constructor(private router: Router) {}

  logout(): void {
    localStorage.removeItem('usuarioLogado');
    this.router.navigate(['/login']);
  }
}
