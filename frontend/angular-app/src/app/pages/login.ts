import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { apiError } from '../services/bank-api';
import { Session } from '../services/session';

@Component({
  selector: 'app-login',
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  private readonly session = inject(Session);
  protected email = '';
  protected password = '';
  protected readonly submitting = signal(false);
  protected readonly error = signal('');

  protected async submit(): Promise<void> {
    if (this.submitting() || !this.email.trim() || !this.password) return;
    this.error.set('');
    this.submitting.set(true);
    try {
      await this.session.login(this.email.trim(), this.password);
    } catch (error) {
      this.error.set(apiError(error, {
        400: 'Confirma o endereço de email e a palavra-passe.',
        401: 'Email ou palavra-passe incorrectos.',
      }));
    } finally {
      this.password = '';
      this.submitting.set(false);
    }
  }
}
