import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import {
  Cliente,
  ClienteAprovadoResponseModel
} from '../../models/BarrelFile';
import { environment } from '../../environment';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ClienteService {
  private API_URL = environment.apiUrl;
  private http = inject(HttpClient);

  autoCadastrar(cliente: Cliente): Observable<any> {
    return this.http.post(`${this.API_URL}/clientes`, cliente);
  }

  getCliente(cpf: string): Observable<Cliente> {
    return this.http.get<Cliente>(`${this.API_URL}/clientes/${cpf}`);
  }

  getClientesParaAprovar(): Observable<Cliente[]> {
    return this.http.get<Cliente[]>(`${this.API_URL}/clientes?filtro=para_aprovar`);
  }

  getMelhoresClientes(): Observable<Cliente[]> {
    return this.http.get<Cliente[]>(`${this.API_URL}/clientes?filtro=melhores_clientes`);
  }

  getClientes(nome: string): Observable<Cliente[]> {
    return this.http.get<Cliente[]>(`${this.API_URL}/clientes?nome=${nome}`);
  }

  alterarCliente(cpf: string, cliente: Cliente | any): Observable<any> {
    return this.http.put(`${this.API_URL}/clientes/${cpf}`, cliente);
  }
  aprovarCliente(cpf: string): Observable<ClienteAprovadoResponseModel>{
    return this.http.post<ClienteAprovadoResponseModel>(`${this.API_URL}/clientes/${cpf}/aprovar`, null);
  }

  rejeitarCliente(cpf: string, motivo: string): Observable<any>{
    return this.http.post(`${this.API_URL}/clientes/${cpf}/rejeitar`, { motivo: motivo });
  }


}
