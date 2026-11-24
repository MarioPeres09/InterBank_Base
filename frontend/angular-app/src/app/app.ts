import { Component } from '@angular/core';

import { BankDashboard } from './pages/bank-dashboard';

@Component({
  selector: 'app-root',
  imports: [BankDashboard],
  template: '<app-bank-dashboard />',
})
export class App {}
