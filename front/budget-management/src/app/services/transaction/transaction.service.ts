import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Transaction } from '../../models/transaction.model';


@Injectable({
  providedIn: 'root'
})
export class TransactionService {
  private apiUrl = 'http://localhost:8081/api/transactions';

  constructor(private http: HttpClient) {}

  uploadFile(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);

    return this.http.post(`${this.apiUrl}/upload`, formData);
  }

  getAllTransactions(): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(this.apiUrl);
  }

  getTransactionsByUser(): Observable<Transaction[]> {
  return this.http.get<Transaction[]>('http://localhost:8081/api/transactions/user');
}

assignCategorieToTransaction(transactionId: number, categorieId: number): Observable<Transaction> {
  return this.http.put<Transaction>(
    `${this.apiUrl}/assignCategoryToTransaction/${transactionId}/categorie/${categorieId}`,
    {}
  );
}
}