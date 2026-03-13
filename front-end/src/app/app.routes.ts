import { Routes } from '@angular/router';
import { Homepage } from './pages/sem-perfil/homepage/homepage';
import { Login } from './pages/sem-perfil/login/login';
import { Autocadastro } from './pages/sem-perfil/autocadastro/autocadastro';
import { InicialCliente } from './pages/cliente/inicial-cliente/inicial-cliente';
import { PerfilCliente } from './pages/cliente/perfil-cliente/perfil-cliente';

export const routes: Routes = [
  { path: '', component: Homepage, pathMatch: 'full' },
  { path: 'login', component: Login, title: 'TADS BANK | Login' },
  { path: 'autocadastro', component: Autocadastro, title: 'TADS BANK | Autocadastro' },
  { path: 'cliente', component: InicialCliente, title: 'TADS BANK | Área do cliente' },
  { path: 'cliente-perfil', component: PerfilCliente, title: 'TADS BANK | Meu Perfil ' }
];
