// src/app/services/auth.service.ts
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/auth';

  login(credentials: any): Observable<string> {
    return this.http.post(`${this.apiUrl}/login`, credentials, { responseType: 'text' });
  }

  signup(userData: any): Observable<string> {
    // Această metodă va declanșa INSERT-ul în baza de date 
    return this.http.post(`${this.apiUrl}/signup`, userData, { responseType: 'text' });
  }
}