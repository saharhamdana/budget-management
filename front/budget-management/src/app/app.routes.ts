import { Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';

import { TransactionsComponent } from './transactions/transactions.component';
import { BudgetsComponent } from './budgets/budgets.component';
import { DashboardComponent } from './dashboard/dashboard.component';



export const routes: Routes = [
  { path: 'home', component: HomeComponent },
  { path: '', redirectTo: 'home', pathMatch: 'full' },
 { path: 'dashboard', component: DashboardComponent },
  { path: 'transactions', component: TransactionsComponent },
  { path: 'budget', component: BudgetsComponent },
];

