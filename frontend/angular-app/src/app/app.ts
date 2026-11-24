import { Component, inject } from '@angular/core';

import { BankDashboard } from './pages/bank-dashboard';
import { Login } from './pages/login';
import { Session } from './services/session';

@Component({
  selector: 'app-root',
  imports: [BankDashboard, Login],
  template: '@if (session.customer()) { <app-bank-dashboard /> } @else { <app-login /> }',
})
export class App {
  protected readonly session = inject(Session);
}
