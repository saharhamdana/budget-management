import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { KeycloakService } from '../services/keycloack/keycloak.service';
import { NavbarComponent } from '../navbar/navbar.component';
import * as XLSX from 'xlsx';
import { TransactionService } from '../services/transaction/transaction.service';
import { HttpClient, HttpClientModule, HttpHeaders } from '@angular/common/http';
import { SidebarComponent } from "../sidebar/sidebar.component";
import { RouterOutlet } from "../../../node_modules/@angular/router/router_module.d-Bx9ArA6K";
import * as bootstrap from 'bootstrap';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {
  selectedFile: File | null = null;

  transactions: any[] = [];           // Toutes les transactions reçues du serveur
  filteredTransactions: any[] = [];   // Transactions filtrées à afficher

  filterName: string = '';
  globalFilter: string = '';
  newTransactions: any[] = []; 
  categories: any[] = [];
  selectedTransactionId: number | null = null;
  selectedCategorieId: number | null = null;

  objectKeys = Object.keys;  // Pour récupérer les clés dynamiquement dans le template

  constructor(private http: HttpClient) {}
 



  // const headers = new HttpHeaders({
  //   Authorization: `Bearer ${token}`
  // });

  // this.http.get<any[]>('http://localhost:8081/api/transactions/user', { headers })
  //   .subscribe({
  //     next: (data) => {
  //       this.transactions = data;
  //       this.filteredTransactions = [...this.transactions];
  //       console.log('Toutes les transactions chargées');
  //     },
  //     error: (err) => {
  //       console.error('Erreur lors du chargement des transactions :', err);
  //     }
  //   });
  //   }
  // Gestion du fichier sélectionné
  handleFileInput(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length) {
      this.selectedFile = input.files[0];
      this.uploadFile();  // Lancer directement l'upload
    }
  }

  ngOnInit(): void {
  this.loadTransactions();
  this.loadCategories();
}

loadTransactions() {
  const token = localStorage.getItem('token');
  const headers = new HttpHeaders({ Authorization: `Bearer ${token}` });

  this.http.get<any[]>('http://localhost:8081/api/transactions/user', { headers })
    .subscribe({
      next: (data) => {
        this.transactions = data;
        this.filteredTransactions = [...this.transactions];
      },
      error: (err) => {
        console.error('Erreur chargement transactions :', err);
      }
    });
}

loadCategories() {
  const token = localStorage.getItem('token');
  const headers = new HttpHeaders({ Authorization: `Bearer ${token}` });

  this.http.get<any[]>('http://localhost:8081/api/categorie', { headers }) // adapte l'URL si besoin
    .subscribe({
      next: (data) => {
        this.categories = data;
      },
      error: (err) => {
        console.error('Erreur chargement catégories :', err);
      }
    });
}

affecterCategorie(transaction: any) {
  this.selectedTransactionId = transaction.id;
  this.selectedCategorieId = null;
  const modal = new bootstrap.Modal(document.getElementById('categorieModal')!);
  modal.show();
}

confirmerAffectation() {
  if (!this.selectedTransactionId || !this.selectedCategorieId) return;

  const token = localStorage.getItem('token');
  const headers = new HttpHeaders({ Authorization: `Bearer ${token}` });

  this.http.put<any>(
    `http://localhost:8081/api/transactions/assignCategoryToTransaction/${this.selectedTransactionId}/categorie/${this.selectedCategorieId}`,
    {},
    { headers }
  ).subscribe({
    next: () => {
      alert('✅ Catégorie affectée avec succès !');
      this.selectedTransactionId = null;
      this.selectedCategorieId = null;
      this.loadTransactions();

      // Fermer le modal
      const modalEl = document.getElementById('categorieModal');
      if (modalEl) bootstrap.Modal.getInstance(modalEl)?.hide();
    },
    error: (err) => {
      console.error('Erreur assignation catégorie :', err);
      alert('❌ Échec lors de l\'affectation.');
    }
  });
}

confirmCategorieAffectation(event: Event) {
  const selectElement = event.target as HTMLSelectElement;
  const categorieId = selectElement.value;
  console.log('Catégorie sélectionnée :', categorieId);
  if (!this.selectedTransactionId) return;

  const token = localStorage.getItem('token');
  const headers = new HttpHeaders({ Authorization: `Bearer ${token}` });

  this.http.put<any>(
    `http://localhost:8081/api/transaction/assignCategoryToTransaction/${this.selectedTransactionId}/categorie/${categorieId}`,
    {},
    { headers }
  ).subscribe({
    next: () => {
      alert('✅ Catégorie affectée avec succès !');
      this.selectedTransactionId = null;
      this.loadTransactions(); // pour recharger les données avec la catégorie affectée
    },
    error: (err) => {
      console.error('Erreur assignation catégorie :', err);
      alert('❌ Échec lors de l\'affectation de la catégorie.');
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
        this.filteredTransactions = [...data];
        localStorage.setItem('newTransactions', JSON.stringify(data)); // 🧠 Stocker les nouvelles
      }
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
  // Filtrage des transactions (par nom et global)
  filterTransactions() {
    this.filteredTransactions = this.transactions.filter(tx => {
      const matchName = this.filterName ? (tx.name?.toLowerCase().includes(this.filterName.toLowerCase())) : true;

      const globalText = JSON.stringify(tx).toLowerCase();
      const matchGlobal = this.globalFilter ? globalText.includes(this.globalFilter.toLowerCase()) : true;

      return matchName && matchGlobal;
    });
  }
  sidebarVisible: boolean = false;

toggleSidebar() {
  this.sidebarVisible = !this.sidebarVisible;
}
isMobile(): boolean {
  return window.innerWidth < 768;
}



}

