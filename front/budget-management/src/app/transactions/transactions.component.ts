import { Component, OnInit } from '@angular/core';
import { TransactionService } from '../services/transaction/transaction.service';
import { Transaction } from '../models/transaction.model';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-transactions',
  imports: [CommonModule],
  templateUrl: './transactions.component.html',
  styleUrls: ['./transactions.component.css']
})
export class TransactionsComponent implements OnInit {
  transactions: Transaction[] = [];

  constructor(private transactionService: TransactionService) {}

  ngOnInit(): void {
    this.transactionService.getTransactionsByUser().subscribe((data) => {
      this.transactions = data;
    });
  }
}