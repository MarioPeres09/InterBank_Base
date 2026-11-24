import { HttpClient, HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { firstValueFrom, tap } from 'rxjs';

interface CustomerSession {
  token: string;
  expiresAt: string;
  customerId: number;
  customerName: string;
}

const storageKey = 'interbank.customer-session';

@Injectable({ providedIn: 'root' })
export class Session {
  private readonly http = inject(HttpClient);
  private readonly current = signal<CustomerSession | null>(null);
  private expiryTimer?: ReturnType<typeof setTimeout>;
  readonly customer = this.current.asReadonly();

  constructor() {
    try {
      const saved = sessionStorage.getItem(storageKey);
      if (saved) this.setSession(JSON.parse(saved) as CustomerSession);
    } catch {
      this.logout();
    }
  }

  async login(email: string, password: string): Promise<void> {
    const session = await firstValueFrom(this.http.post<CustomerSession>('/api/auth/login', { email, password }));
    this.setSession(session);
  }

  logout(): void {
    clearTimeout(this.expiryTimer);
    this.current.set(null);
    this.persist(null);
  }

  private setSession(session: CustomerSession): void {
    const remaining = Date.parse(session.expiresAt) - Date.now();
    if (!session.token || !session.customerId || !Number.isFinite(remaining) || remaining <= 0) {
      this.logout();
      return;
    }
    clearTimeout(this.expiryTimer);
    this.current.set(session);
    this.persist(session);
    this.expiryTimer = setTimeout(() => this.logout(), remaining);
  }

  private persist(session: CustomerSession | null): void {
    try {
      if (session) sessionStorage.setItem(storageKey, JSON.stringify(session));
      else sessionStorage.removeItem(storageKey);
    } catch {
      // The session remains usable in memory when storage is unavailable.
    }
  }
}

export const attachSession: HttpInterceptorFn = (request, next) => {
  if (!request.url.startsWith('/api/') || request.url === '/api/auth/login') return next(request);
  const session = inject(Session);
  const token = session.customer()?.token;
  if (!token) return next(request);

  return next(request.clone({ setHeaders: { Authorization: `Bearer ${token}` } })).pipe(
    tap({
      error: (error: unknown) => {
        if (error instanceof HttpErrorResponse && error.status === 401 && session.customer()?.token === token) {
          session.logout();
        }
      },
    }),
  );
};
