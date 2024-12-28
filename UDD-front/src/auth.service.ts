import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private baseUrl = 'http://localhost:8080/api/auth'; // Backend API URL

  constructor(private http: HttpClient, private router: Router) {}

  login(credentials: { username: string; password: string }): Observable<any> {
    return this.http.post(`${this.baseUrl}/login`, credentials, { observe: 'response' });
  }

  register(data: { username: string; password: string }): Observable<any> {
    return this.http.post(`${this.baseUrl}/register`, data);
  }

  saveToken(token: string): void {
    localStorage.setItem('jwtToken', token);
  }

  getToken(): string | null {
    return localStorage.getItem('jwtToken');
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  logout(): void {
    localStorage.removeItem('jwtToken');
    this.router.navigate(['/login']);
  }
}
