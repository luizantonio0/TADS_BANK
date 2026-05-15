import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {Observable} from 'rxjs';
import { ViacepEndereco } from '../models/cep.model';

@Injectable({
  providedIn: 'root',
})
export class CEPService {
  private http = inject(HttpClient);

  buscarCEP(cep: string): Observable<ViacepEndereco> {
    return this.http.get<ViacepEndereco>(`https://viacep.com.br/ws/${cep.replaceAll("\\D", "")}/json/`);
  }

}
