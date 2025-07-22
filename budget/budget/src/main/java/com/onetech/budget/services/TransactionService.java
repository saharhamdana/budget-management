package com.onetech.budget.services;

import com.onetech.budget.DTO.TransactionRequestDTO;
import com.onetech.budget.models.Categorie;
import com.onetech.budget.models.Client;
import com.onetech.budget.models.Transaction;
import com.onetech.budget.repositories.CategorieRepository;
import com.onetech.budget.repositories.ClientRepository;
import com.onetech.budget.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final ClientRepository clientRepository;
    private final CategorieRepository categorieRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, ClientRepository clientRepository, CategorieRepository categorieRepository) {
        this.transactionRepository = transactionRepository;
        this.clientRepository = clientRepository;
        this.categorieRepository = categorieRepository;
    }

    public Transaction saveTransaction(TransactionRequestDTO dto) {
       /* Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new RuntimeException("Client non trouvé avec l'id " + dto.getClientId()));
*/
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = jwt.getSubject(); // 'sub' contient généralement l'ID utilisateur

        Transaction transaction = new Transaction();
        transaction.setMontant(dto.getMontant());
        transaction.setDateOpertation(dto.getDateOpertation());
        transaction.setDateValeur(dto.getDateValeur());
        transaction.setClient(userId);

        //transaction.setClient(client);

        return transactionRepository.save(transaction);
    }

    public Transaction assignCategorieToTransaction(Long transactionId, Long categorieId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction non trouvée"));

        Categorie categorie = categorieRepository.findById(categorieId)
                .orElseThrow(() -> new RuntimeException("Catégorie non trouvée"));

        transaction.setCategorie(categorie);
        return transactionRepository.save(transaction);
    }


    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    public Transaction findById(Long id) {
        return transactionRepository.findById(id).orElse(null);
    }

    public Transaction deleteById(Long id) {
        Transaction transaction = findById(id);
        transactionRepository.deleteById(id);
        return transaction;
    }
}