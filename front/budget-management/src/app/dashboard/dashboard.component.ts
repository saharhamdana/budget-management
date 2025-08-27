import { Component, ElementRef, ViewChild, AfterViewInit, NgZone, inject } from '@angular/core';

import { Chart, registerables } from 'chart.js';
import { DashboardService } from '../services/dashboard/dashboard.service';
import { CommonModule } from '@angular/common';

Chart.register(...registerables);
@Component({
  selector: 'app-dashboard',
  imports: [CommonModule],
  standalone: true,
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements AfterViewInit {
  summary: any = null;
  loading = true;

  @ViewChild('budgetVsRealChart') budgetVsRealChart!: ElementRef<HTMLCanvasElement>;
  @ViewChild('byCategorieChart') byCategorieChart!: ElementRef<HTMLCanvasElement>;
  @ViewChild('totalBudgetVsSpentChart') totalBudgetVsSpentChart!: ElementRef<HTMLCanvasElement>;


  constructor(private dashboardService: DashboardService) {}

  ngOnInit() {
    this.dashboardService.getSummary().subscribe({
      next: (data) => {
        console.log("📊 Données reçues :", data);
        this.summary = data;
        this.loading = false;

        // 🟢 on appelle les charts APRES que le DOM soit prêt
        this.tryCreateCharts();
      },
      error: (err) => {
        console.error("Erreur API :", err);
        this.loading = false;
      }
    });
  }

  ngAfterViewInit() {
    // 🟢 si les données sont déjà dispo, on peut créer directement
    this.tryCreateCharts();
  }

 tryCreateCharts() {
  if (!this.summary) {
    console.log("⏳ Données pas encore prêtes...");
    return;
  }
  if (!this.budgetVsRealChart || !this.byCategorieChart) {
    console.log("⚠️ Canvas pas encore dispo !");
    // 🔧 on retente après un tick Angular
    setTimeout(() => this.tryCreateCharts(), 50);
    return;
  }

  console.log("✅ Création des charts...");
  this.createCharts();
}


createCharts() {
  // Chart 1: Budget vs Réel
  new Chart(this.budgetVsRealChart.nativeElement, {
    type: 'bar',
    data: {
      labels: this.summary.budgetVsReal.map((x: any) => x.categorie),
      datasets: [
        {
          label: 'Budget',
          data: this.summary.budgetVsReal.map((x: any) => x.budget),
          backgroundColor: '#082c6c',   // bleu nuit
          borderColor: '#0d1b2a',
          borderWidth: 1
        },
        {
          label: 'Réel',
          data: this.summary.budgetVsReal.map((x: any) => x.realSpent),
          backgroundColor: '#f06c14',  // orange foncé
          borderColor: '#f06c14',
          borderWidth: 1
        }
      ]
    },
    options: {
      responsive: true,
      plugins: {
        legend: {
          labels: { color: '#082c6c' }
        }
      },
      scales: {
        x: { ticks: { color: '#082c6c' } },
        y: { ticks: { color: '#082c6c' } }
      }
    }
  });

  // Chart 2: Répartition par Catégorie
  new Chart(this.byCategorieChart.nativeElement, {
    type: 'pie',
    data: {
      labels: Object.keys(this.summary.byCategorie),
      datasets: [{
        data: Object.values(this.summary.byCategorie),
        backgroundColor: [
          '#082c6c', // bleu nuit
          '#0d1b2a', // bleu plus sombre
          '#f06c14', // orange foncé
          '#DAAB3A', // orange accent
          '#4A919E', // violet foncé
          '#F27438', // vert foncé
        ]
      }]
    },
    options: {
      plugins: {
        legend: {
          labels: { color: '#082c6c' }
        }
      }
    }
  });
  // Chart 3: Budget Total vs Dépenses Réelles
new Chart(this.totalBudgetVsSpentChart.nativeElement, {
  type: 'doughnut',
  data: {
    labels: ['Budget Total', 'Dépenses Réelles'],
    datasets: [{
      data: [this.summary.totalBudget, this.summary.totalSpent],
      backgroundColor: ['#082c6c', '#f06c14']
    }]
  },
  options: {
    plugins: {
      legend: {
        labels: { color: '#082c6c' }
      }
    }
  }
});

}



  isLoading() {
    return this.loading;
  }
}