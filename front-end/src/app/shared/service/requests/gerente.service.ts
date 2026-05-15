import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environment';
import { Observable } from 'rxjs';
import { Gerente, GerenteResponse } from '../../models/gerente.model';

@Injectable({
  providedIn: 'root',
})
export class GerenteService {
  private API_URL = environment.apiUrl;
  private http = inject(HttpClient);

  getGerentes(): Observable<GerenteResponse[]> {
    return this.http.get<GerenteResponse[]>(`${this.API_URL}/gerentes`);
  }

  getGerente(cpf: string): Observable<GerenteResponse> {
    return this.http.get<GerenteResponse>(`${this.API_URL}/gerentes/${cpf}`);
  }

  cadastrarGerente(gerente: Gerente): Observable<Gerente> {
    return this.http.post<Gerente>(`${this.API_URL}/gerentes`, gerente);
  }

  editarGerente(cpf: string, gerente: Gerente): Observable<Gerente> {
    return this.http.put<Gerente>(`${this.API_URL}/gerentes/${cpf}`, gerente);
  }

  excluirGerente(cpf: string): Observable<any> {
    return this.http.delete(`${this.API_URL}/gerentes/${cpf}`);
  }
}
