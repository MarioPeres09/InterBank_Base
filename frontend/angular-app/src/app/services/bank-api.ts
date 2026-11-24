import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';

import { Account, CreditReceipt, Transfer, TransferRequest } from '../models/bank';

@Injectable({ providedIn: 'root' })
export class BankApi {
  private readonly http = inject(HttpClient);

  accounts() {
    return this.http.get<Account[]>('/api/accounts');
  }

  transfers() {
    return this.http.get<Transfer[]>('/api/transfers');
  }

  receipt(id: string) {
    return this.http.get<CreditReceipt>(`/api/transfers/${encodeURIComponent(id)}/receipt`);
  }

  send(id: string, request: TransferRequest) {
    return this.http.post<Transfer>('/api/transfers', request, {
      headers: { 'Idempotency-Key': id },
    });
  }
}

export function apiError(error: unknown, messages: Record<number, string> = {}): string {
  if (error instanceof HttpErrorResponse) {
    if (messages[error.status]) return messages[error.status];
    if (error.status === 0 || error.status >= 500) {
      return 'Não foi possível comunicar com o banco. Confirma que o serviço está a correr.';
    }
  }
  return 'O pedido não pôde ser concluído.';
}
