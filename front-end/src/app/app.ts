import { Component, signal } from '@angular/core';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { Header } from './components/header/header';
import { ToastContainerComponent } from './components/toast/toast';
import { filter } from 'rxjs';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, ToastContainerComponent, Header],
  templateUrl: './app.html'
})
export class App {

  protected readonly title = signal('front-end');

  isHome = false;
  isGerente = false;
  isClienteInicial = false;
  isAutocadastro = false;
  isLogin = false;

  constructor(private router: Router) {
    this.router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .subscribe((event: any) => {
        this.isHome = event.url === '/' || event.urlAfterRedirects === '/';
        this.isLogin = event.url === '/login' || event.urlAfterRedirects === '/login';
        this.isAutocadastro = event.url === '/autocadastro' || event.urlAfterRedirects === '/autocadastro';
        this.isClienteInicial = event.url === '/cliente' || event.urlAfterRedirects === '/cliente';
        this.isGerente = event.url === '/gerente' || event.urlAfterRedirects === '/gerente';
      });
  }

}
