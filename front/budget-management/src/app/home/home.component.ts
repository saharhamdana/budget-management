import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule, HttpHeaders } from '@angular/common/http';
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
  filteredTransactions: any[] = [];   // Transactions filtrées à afficher (sans catégorie)
  filterName: string = '';
  globalFilter: string = '';
  categories: any[] = [];
  selectedTransactionId: number | null = null;
  selectedCategorieId: number | null = null;
  objectKeys = Object.keys;

  constructor(private http: HttpClient) {}

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
          // Filter to only show transactions without categories
          this.filteredTransactions = this.transactions.filter(t => !t.categorie);
          console.log("Transactions without categories:", this.filteredTransactions);
        },
        error: (err) => {
          console.error('Erreur chargement transactions :', err);
        }
      });
  }

  loadCategories() {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({ Authorization: `Bearer ${token}` });

    this.http.get<any[]>('http://localhost:8081/api/categorie', { headers })
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
        this.loadTransactions(); // Reload transactions to refresh the list
        
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

  handleFileInput(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length) {
      this.selectedFile = input.files[0];
      this.uploadFile();
    }
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
          // After upload, reload transactions to show only those without categories
          this.loadTransactions();
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

  // Filter transactions without categories
  filterTransactions() {
    let filtered = this.transactions.filter(t => !t.categorie);
    
    // Apply name filter
    if (this.filterName) {
      filtered = filtered.filter(tx => 
        tx.name?.toLowerCase().includes(this.filterName.toLowerCase())
      );
    }
    
    // Apply global filter
    if (this.globalFilter) {
      const searchText = this.globalFilter.toLowerCase();
      filtered = filtered.filter(tx => {
        const globalText = JSON.stringify(tx).toLowerCase();
        return globalText.includes(searchText);
      });
    }
    
    this.filteredTransactions = filtered;
  }

  getCategoryIcon(category: string): string {
    switch (category.toLowerCase()) {
      case 'shopping': return 'fas fa-shopping-cart';
      case 'car': return 'fas fa-car';
      case 'sport': return 'fas fa-basketball-ball';
      case 'animals': return 'fas fa-paw';
      case 'health': return 'fas fa-heartbeat';
      case 'food': return 'fas fa-utensils';
      case 'technology': return 'fas fa-laptop-code';
      case 'education': return 'fas fa-book';
      case 'travel': return 'fas fa-plane';
      case 'entertainment': return 'fas fa-film';
      case 'finance': return 'fas fa-wallet';
      case 'clothing': return 'fas fa-tshirt';
      case 'beauty': return 'fas fa-magic';
      case 'home': return 'fas fa-home';
      case 'work': return 'fas fa-briefcase';
      default: return 'fas fa-folder-open';
    }
  }
}