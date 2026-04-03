import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
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

  //Retorno vazio
  autoCadastrar(cliente: Cliente): Observable<any> {
    return this.http.post(`${this.API_URL}/clientes`, cliente);
  }

  getCliente(cpf: string): Observable<Cliente> {
    return this.http.get<Cliente>(`${this.API_URL}/clientes/${cpf}`);
  }

  getClientes(): Observable<Cliente[]> {
    return this.http.get<Cliente[]>(`${this.API_URL}/clientes`);
  }

  alterarCliente(cliente: Cliente): Observable<any> {
    return this.http.put(`${this.API_URL}/clientes`, cliente);
  }
  aprovarCliente(cpf: string): Observable<ClienteAprovadoResponseModel>{
    return this.http.post<ClienteAprovadoResponseModel>(`${this.API_URL}/${cpf}/aprovar`, null);
  }

  rejeitarCliente(cpf: string): Observable<any>{
    return this.http.post(`${this.API_URL}/${cpf}/rejeitar`, null);
  }


}
