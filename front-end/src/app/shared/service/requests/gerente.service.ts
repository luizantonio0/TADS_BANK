import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environment';
import { Observable } from 'rxjs';
import { Gerente, GerenteDashboardResponse } from '../../models/gerente.model';
import { CadastroGerenteDTO } from '../../models/resquest/cadastrogerente.model';
import { EditarGerenteDTO } from '../../models/resquest/editgerente.model';

@Injectable({
  providedIn: 'root',
})
export class GerenteService {
  private API_URL = environment.apiUrl;
  private http = inject(HttpClient);

  getGerentes(): Observable<Gerente[]> {
    return this.http.get<Gerente[]>(`${this.API_URL}/gerentes`);
  }

  getGerentesDashboard(): Observable<GerenteDashboardResponse[]> {
    return this.http.get<GerenteDashboardResponse[]>(`${this.API_URL}/gerentes?filtro=dashboard`);
  }

  getGerente(cpf: string): Observable<Gerente> {
    return this.http.get<Gerente>(`${this.API_URL}/gerentes/${cpf}`);
  }

  cadastrarGerente(gerente: CadastroGerenteDTO): Observable<Gerente> {
    return this.http.post<Gerente>(`${this.API_URL}/gerentes`, gerente);
  }

  editarGerente(cpf: string, gerente: EditarGerenteDTO): Observable<Gerente> {
    return this.http.put<Gerente>(`${this.API_URL}/gerentes/${cpf}`, gerente);
  }

  excluirGerente(cpf: string): Observable<any> {
    return this.http.delete(`${this.API_URL}/gerentes/${cpf}`);
  }
}
