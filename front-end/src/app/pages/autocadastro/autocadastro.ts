import { Component } from '@angular/core';
import { Header } from '../../components/header/header';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-autocadastro',
  imports: [Header, FormsModule],
  templateUrl: './autocadastro.html',
  styleUrl: './autocadastro.css',
})
export class Autocadastro {
  cpf: string = '';
  nome: string = '';
  telefone: string = '';
  email: string = '';
  password: string = '';
  endereco: string = '';
  cep: string = '';
  cidade: string = '';
  estado: string = '';
  salario: string = '';
}
