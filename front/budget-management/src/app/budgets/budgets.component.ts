import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { SidebarComponent } from "../sidebar/sidebar.component";
import { CategorieService } from '../services/categorie/categorie.service';
import { Categorie } from '../models/categorie.model';
import { BudgetService } from '../services/budgets/budget.service';
import { Budget } from '../models/budget.model';



@Component({
  selector: 'app-budgets',
  imports: [FormsModule, CommonModule],
  templateUrl: './budgets.component.html',
  styleUrl: './budgets.component.css'
})
export class BudgetsComponent implements OnInit {
  categories: Categorie[] = [];
  newBudget = {
    categorie: null as Categorie | null,
    montant: null
  };
  budgets: any[] = [];
  budgetEnEdition: number | null = null;

  constructor(
    private categorieService: CategorieService,
    private budgetService: BudgetService
  ) {}

  ngOnInit(): void {
    this.loadCategories();
    this.loadBudgets();
  }

  loadCategories() {
    this.categorieService.getCategories().subscribe(data => {
      this.categories = data;
    }, error => {
      console.error('Erreur lors du chargement des catégories:', error);
    });
  }

  loadBudgets() {
  this.budgetService.getBudgets().subscribe(data => {
    this.budgets = data;
  }, error => {
    console.error('Erreur chargement budgets:', error);
  });
}

 ajouterBudget() {
  if (!this.newBudget.categorie || !this.newBudget.montant || this.newBudget.montant <= 0) {
    alert('Veuillez renseigner une catégorie valide et un montant supérieur à 0');
    return;
  }

  const budgetToSend: Budget = {
    amountPerMonth: this.newBudget.montant,
    categorie: this.newBudget.categorie
  };

  this.budgetService.addBudget(budgetToSend).subscribe({
    next: (savedBudget) => {
      console.log('Budget ajouté avec succès:', savedBudget);
      this.newBudget = { categorie: null, montant: null };

      // Recharge les budgets depuis l’API pour avoir la version FIXE
      this.loadBudgets();
    },
    error: (err) => {
      console.error('Erreur lors de l\'ajout du budget:', err);
      alert('Erreur lors de l\'ajout du budget.');
    }
  });
}




  supprimerBudget(id: number) {
    this.budgetService.deleteBudget(id).subscribe({
      next: () => this.loadBudgets(),
      error: err => console.error('Erreur suppression:', err)
    });
  }

  modifierBudget(budget: any) {
  console.log('Budget à modifier :', budget);
  this.newBudget.categorie = budget.categorie;
  this.newBudget.montant = budget.amountPerMonth;
  this.budgetEnEdition = budget.id;
}

  enableEdit(budget: Budget) {
    budget.editing = true;
  }

  saveBudget(budget: Budget) {
  if (budget.id === undefined) {
    console.error("Impossible de mettre à jour un budget sans ID");
    return;
  }

  this.budgetService.updateBudget(budget.id, budget.amountPerMonth, budget.categorie)
    .subscribe({
      next: (updated: Budget) => {
        budget.amountPerMonth = updated.amountPerMonth;
        budget.editing = false;
      },
      error: (err) => console.error(err)
    });
}
   
onUpdateRealAmount(budgetId: number) {
    this.budgetService.updateRealAmountFromTransactions(budgetId).subscribe({
      next: (updatedBudget) => {
        // Met à jour la liste localement
        this.budgets = this.budgets.map(b => b.id === updatedBudget.id ? updatedBudget : b);
        alert('Montant réel mis à jour avec succès');
      },
      error: (err) => {
        console.error(err);
        alert('Erreur lors de la mise à jour');
      }
    });
  }

}
