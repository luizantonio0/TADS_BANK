import { Routes } from '@angular/router';
import { Homepage } from './pages/homepage/homepage';
import { Login } from './pages/login/login';
import { Autocadastro } from './pages/autocadastro/autocadastro';

export const routes: Routes = [
  { path: '', component: Homepage, pathMatch: 'full' },
  { path: 'login', component: Login, title: 'TADS BANK | Login' },
  { path: 'autocadastro', component: Autocadastro, title: 'TADS BANK | Autocadastro' },
];
