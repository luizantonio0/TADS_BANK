import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { LoginRequestModel, LoginResponseModel, LogoutResponseModel } from '../../models/BarrelFile';
import { environment } from '../../environment';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private API_URL = environment.apiUrl;
  private http = inject(HttpClient);

  login(login: LoginRequestModel): Observable<LoginResponseModel> {
    return this.http.post<LoginResponseModel>(`${this.API_URL}/login`, login);
  }

  logout() {
    return this.http.post<LogoutResponseModel>(`${this.API_URL}/logout`, {});
  }
}
