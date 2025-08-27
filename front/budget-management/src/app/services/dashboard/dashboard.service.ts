import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {

  private apiUrl = 'http://localhost:8081/api/dashboard';

  constructor(private http: HttpClient) { }

  getSummary(): Observable<any> {
    const token = localStorage.getItem('token'); // JWT après login
    if (!token) throw new Error('Utilisateur non authentifié');
    console.log("testttttttttttttttttt");
    
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    return this.http.get(`${this.apiUrl}/summary`, { headers });
  }
}
