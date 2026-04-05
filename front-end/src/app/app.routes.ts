import { Routes } from '@angular/router';
import { Homepage } from './pages/sem-perfil/homepage/homepage';
import { Login } from './pages/sem-perfil/login/login';
import { Autocadastro } from './pages/sem-perfil/autocadastro/autocadastro';
import { InicialCliente } from './pages/cliente/inicial-cliente';
import { InicialGerente } from './pages/gerente/inicial-gerente';
import { InicialAdministrador } from './pages/administrador/inicial-administrador';

export const routes: Routes = [
  { path: '', component: Homepage, pathMatch: 'full' },
  { path: 'login', component: Login, title: 'TADS BANK | Login' },
  { path: 'autocadastro', component: Autocadastro, title: 'TADS BANK | Autocadastro' },
  { path: 'cliente', component: InicialCliente, title: 'TADS BANK | Área do cliente' },
  { path: 'gerente', component: InicialGerente, title: 'TADS BANK | Área do gerente' },
  { path: 'administrador', component: InicialAdministrador, title: 'TADS BANK | Área do administrador' }
];
