import { computed, inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';
import { type Observable, tap } from 'rxjs';
import type AuthState from './auth-service-types';
import type { BackendAuthResponse, BackendSignUpResponse } from './auth-service-types';
import { jwtDecode } from 'jwt-decode';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);

  private API_URL = environment.apiUrl + 'auth';

  private authState = signal<AuthState>(this.restoreState());

  public isLoggedIn = computed(() => !!this.authState().token);
  public getToken = computed(() => this.authState().token);
  public getUserId = computed(() => this.authState().userId);
  public getUserName = computed(() => this.authState().name);
  public getUserEmail = computed(() => this.authState().email);
  public getRoles = computed(() => this.authState().roles);
  public isEmployee = computed(() => {
    const roles = this.authState().roles;
    console.log('Sprawdzanie ról użytkownika:', roles);
    return roles?.includes('ROLE_EMPLOYEE') || roles?.includes('ROLE_MANAGER') || false;
  });
  login(credentials: { email: string; password: string }): Observable<BackendAuthResponse> {
    return this.http.post<BackendAuthResponse>(`${this.API_URL}/login`, credentials).pipe(
      tap((response) => {
        const jwtDecoded: any = jwtDecode(response.data.token);
        this.authState.set({
          token: response.data.token,
          roles: response.data.roles,
          userId: jwtDecoded.id,
          email: jwtDecoded.sub,
          name: jwtDecoded.name,
        });
        localStorage.setItem('authToken', response.data.token);
      }),
    );
  }
  private restoreState(): AuthState {
    const token = localStorage.getItem('authToken');
    if (token) {
      try {
        const decodedToken: any = jwtDecode(token);
        const isExpired = decodedToken.exp * 1000 < Date.now();
        const userId = decodedToken.id;
        if (isExpired) {
          localStorage.removeItem('authToken');
          return {
            token: null,
            roles: null,
            userId: null,
            email: null,
            name: null,
          };
        }
        return {
          token: token,
          roles: decodedToken.roles || null,
          userId: userId || null,
          email: decodedToken.sub || null,
          name: decodedToken.name || null,
        };
      } catch (e) {
        localStorage.removeItem('authToken');
        return {
          token: null,
          roles: null,
          userId: null,
          email: null,
          name: null,
        };
      }
    }
    return {
      token: null,
      roles: null,
      userId: null,
      email: null,
      name: null,
    };
  }
  signUp(signupData: {
    email: string;
    password: string;
    name: string;
    phone: string;
  }): Observable<BackendSignUpResponse> {
    return this.http.post<BackendSignUpResponse>(`${this.API_URL}/signup`, signupData).pipe(
      tap((response) => {
        console.log('Rejestracja udana:', response);
      }),
    );
  }
  logout(): void {
    this.authState.set({
      token: null,
      roles: null,
      userId: null,
      email: null,
      name: null,
    });
    localStorage.removeItem('authToken');
    void this.router.navigate(['/login']);
  }

  forgotPassword(email: string): Observable<{ message: string }> {
    return this.http.post<{ message: string }>(`${this.API_URL}/forgot-password`, { email });
  }

  resetPassword(token: string, newPassword: string): Observable<{ message: string }> {
    return this.http.post<{ message: string }>(`${this.API_URL}/reset-password`, {
      token,
      newPassword,
    });
  }
}
