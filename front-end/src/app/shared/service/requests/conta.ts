import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environment';
import { Observable } from 'rxjs';
import { ContaResponse, OperacaoContaResponse } from '../../models/conta.model';


@Injectable({
  providedIn: 'root',
})
export class ContaService {
  private API_URL = environment.apiUrl;
  private http = inject(HttpClient);

  depositar(numeroConta: string, valor: number): Observable<OperacaoContaResponse> {
    return this.http.post<OperacaoContaResponse>(
        `${this.API_URL}/contas/${numeroConta}/depositar`,
    {valor}
    );
  }

  sacar(numeroConta: string, valor: number): Observable<OperacaoContaResponse> {
    return this.http.post<OperacaoContaResponse>(
      `${this.API_URL}/contas/${numeroConta}/sacar`,
    {valor}
    );
  }

  transferir(origem: string, destino: string, valor: number): Observable<any> {
    return this.http.post<any>(
      `${this.API_URL}/contas/${origem}/transferir`,
      {destino, valor}
    );
  }

}
