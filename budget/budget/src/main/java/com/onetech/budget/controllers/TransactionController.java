package com.onetech.budget.controllers;

import com.onetech.budget.DTO.TransactionRequestDTO;
import com.onetech.budget.models.Transaction;
import com.onetech.budget.repositories.UploadedFileRepository;
import com.onetech.budget.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "http://localhost:4200")
public class TransactionController {


    private final TransactionService transactionService;
    @Autowired
    public TransactionController(TransactionService transactionService ) {
        this.transactionService = transactionService;
    }

    private UploadedFileRepository uploadedFileRepository;
    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody TransactionRequestDTO dto) {
        Transaction saved = transactionService.saveTransaction(dto);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/upload")
    public ResponseEntity<List<Transaction>> uploadTransactions(@RequestParam("file") MultipartFile file) {
        try {
            List<Transaction> savedTransactions = transactionService.processExcelFile(file);
            return ResponseEntity.ok(savedTransactions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/all")
    public List<Transaction> getAllTransactions() {
        return this.transactionService.findAll();
    }
    @DeleteMapping("/{id}")
    public void deleteTransaction(@PathVariable Long id) {
        this.transactionService.deleteById(id);
    }

    @PutMapping("/assignCategoryToTransaction/{transactionId}/categorie/{categorieId}")
    public ResponseEntity<Transaction> affecterCategorie(
            @PathVariable Long transactionId,
            @PathVariable Long categorieId) {
        Transaction updated = transactionService.assignCategorieToTransaction(transactionId, categorieId);
        return ResponseEntity.ok(updated);
    }
    @GetMapping("/user")
    public ResponseEntity<List<Transaction>> getTransactionsForCurrentUser() {
        List<Transaction> transactions = transactionService.findByClientConnected();
        return ResponseEntity.ok(transactions);
    }


}