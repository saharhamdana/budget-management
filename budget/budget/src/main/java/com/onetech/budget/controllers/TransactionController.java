package com.onetech.budget.controllers;

import com.onetech.budget.DTO.TransactionRequestDTO;
import com.onetech.budget.models.Transaction;
import com.onetech.budget.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    @Autowired
    public TransactionController(TransactionService transactionService ) {
        this.transactionService = transactionService;
    }
    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody TransactionRequestDTO dto) {
        Transaction saved = transactionService.saveTransaction(dto);
        return ResponseEntity.ok(saved);
    }


    @GetMapping
    public List<Transaction> getAllTransactions() {
        return this.transactionService.findAll();
    }
    @DeleteMapping("/{id}")
    public void deleteTransaction(@PathVariable Long id) {
        this.transactionService.deleteById(id);
    }

}