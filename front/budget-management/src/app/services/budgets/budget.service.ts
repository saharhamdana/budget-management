import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Budget } from '../../models/budget.model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class BudgetService {
  private baseUrl = 'http://localhost:8081/api/budgets';

  constructor(private http: HttpClient) {}

  getBudgets(): Observable<Budget[]> {
    return this.http.get<Budget[]>(this.baseUrl);
  }

  addBudget(budget: Budget) {
  const token = localStorage.getItem('token'); // ou autre source de token
  const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

  return this.http.post<Budget>('http://localhost:8081/api/budgets', budget, { headers });
}

  deleteBudget(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
   updateBudget(id: number, montant: number, categorie: any) {
  return this.http.put<Budget>(`${this.baseUrl}/${id}`, {
    categorie: categorie,
    amountPerMonth: montant
  });
}

  updateRealAmountFromTransactions(budgetId: number): Observable<Budget> {
    return this.http.put<Budget>(`${this.baseUrl}/${budgetId}/realAmount/updateFromTransactions`, {});
  }

}
