import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { KeycloakService } from '../services/keycloack/keycloak.service';
import { NavbarComponent } from '../navbar/navbar.component';
import * as XLSX from 'xlsx';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {
  filterName = '';
  filterDate: string = '';
  globalFilter = '';
  selectedFile: File | null = null;
  transactions: any[] = [];
  filteredTransactions: any[] = [];

  constructor(private keycloakService: KeycloakService) {}

  handleFileInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length) {
      this.selectedFile = input.files[0];
      console.log('Selected file:', this.selectedFile.name);
      
      const reader = new FileReader();
      
      reader.onload = (e: any) => {
        const data = new Uint8Array(e.target.result);
        const workbook = XLSX.read(data, { type: 'array' });
        
        // Assuming you want the first sheet
        const firstSheetName = workbook.SheetNames[0];
        const worksheet = workbook.Sheets[firstSheetName];
        
        // Convert to JSON
        this.transactions = XLSX.utils.sheet_to_json(worksheet);
        this.filterTransactions();
      };
      
      reader.readAsArrayBuffer(this.selectedFile);
    }
  }

  filterTransactions(): void {
    this.filteredTransactions = this.transactions.filter(row => {
      const nameMatch = this.filterName === '' || 
        (row['Libellé Opération'] && 
         row['Libellé Opération'].toLowerCase().includes(this.filterName.toLowerCase()));
      
      const dateMatch = this.filterDate === '' || 
        (row['Date opération'] && 
         row['Date opération'].toString().includes(this.filterDate));
      
      const globalMatch = this.globalFilter === '' || 
        Object.values(row).some(
          val => val != null && val.toString().toLowerCase().includes(this.globalFilter.toLowerCase())
        );
      
      return nameMatch && dateMatch && globalMatch;
    });
  }

  objectKeys(obj: any): string[] {
    return Object.keys(obj);
  }

  logout(): void {
    this.keycloakService.logout();
  }
}