import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { KeycloakService } from '../services/keycloack/keycloak.service';
import { NavbarComponent } from '../navbar/navbar.component';
import * as XLSX from 'xlsx';
import { TransactionService } from '../services/transaction/transaction.service';
import { HttpClient, HttpClientModule, HttpHeaders } from '@angular/common/http';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent, HttpClientModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {
  selectedFile: File | null = null;

  transactions: any[] = [];           // Toutes les transactions reçues du serveur
  filteredTransactions: any[] = [];   // Transactions filtrées à afficher

  filterName: string = '';
  globalFilter: string = '';

  objectKeys = Object.keys;  // Pour récupérer les clés dynamiquement dans le template

  constructor(private http: HttpClient) {}

  // Gestion du fichier sélectionné
  handleFileInput(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length) {
      this.selectedFile = input.files[0];
      this.uploadFile();  // Lancer directement l'upload
    }
  }

  // uploadFile() {
  //   if (!this.selectedFile) {
  //     alert('Please select a file first.');
  //     return;
  //   }

  //   const formData = new FormData();
  //   formData.append('file', this.selectedFile, this.selectedFile.name);

  //   // Récupération du token JWT stocké (adapter selon ta gestion du token)
  //   const token = localStorage.getItem('token');

  //   const headers = new HttpHeaders({
  //     Authorization: `Bearer ${token}`
  //   });
  //   console.log('Bearerrrrr ', token);
    
  //   this.http.post<any[]>('http://localhost:8081/api/transactions/upload', formData, { headers })
  //     .subscribe({
  //       next: (data) => {
  //         console.log('Upload success:', data);
  //         this.transactions = data;
  //         this.filteredTransactions = [...this.transactions]; // Initialiser sans filtre
  //       },
  //       error: (err) => {
  //         console.error('Upload error:', err);
  //         alert('Upload failed');
  //       }
  //     });
  // }
  loadAllTransactions() {
  const token = localStorage.getItem('token');
  const headers = new HttpHeaders({
    Authorization: `Bearer ${token}`
  });

  this.http.get<any[]>('http://localhost:8081/api/transactions/all', { headers })
    .subscribe({
      next: (data) => {
        this.transactions = data;
        this.filteredTransactions = [...this.transactions];
        console.log('Toutes les transactions chargées');
      },
      error: (err) => {
        console.error('Erreur lors du chargement des transactions :', err);
      }
    });
}

  uploadFile() {
  if (!this.selectedFile) {
    alert('Please select a file first.');
    return;
  }

  const formData = new FormData();
  formData.append('file', this.selectedFile, this.selectedFile.name);

  const token = localStorage.getItem('token');
  const headers = new HttpHeaders({
    Authorization: `Bearer ${token}`
  });

  this.http.post<any[]>('http://localhost:8081/api/transactions/upload', formData, { headers })
    .subscribe({
      next: (data) => {
        if (data.length === 0) {
          alert('✅ Aucune nouvelle transaction : toutes les références sont déjà enregistrées.');
        } else {
          alert(`✅ ${data.length} nouvelle(s) transaction(s) ajoutée(s).`);
        }

        // 🔁 Recharger toutes les transactions (anciennes + nouvelles)
        this.loadAllTransactions();
         // Initialiser sans filtre
      },
      error: (err) => {
        console.error('Upload error:', err);
        alert('❌ Échec de l\'upload');
      }
    });
}



  getDisplayKeys(obj: any): string[] {
  return Object.keys(obj).filter(key => key !== 'client');
}

affecterCategorie(transaction: any) {
  console.log('Affectation de catégorie à :', transaction);
  // Tu peux ici ouvrir un modal, lancer une requête, etc.
}

  // Filtrage des transactions (par nom et global)
  filterTransactions() {
    this.filteredTransactions = this.transactions.filter(tx => {
      const matchName = this.filterName ? (tx.name?.toLowerCase().includes(this.filterName.toLowerCase())) : true;

      const globalText = JSON.stringify(tx).toLowerCase();
      const matchGlobal = this.globalFilter ? globalText.includes(this.globalFilter.toLowerCase()) : true;

      return matchName && matchGlobal;
    });
  }
}