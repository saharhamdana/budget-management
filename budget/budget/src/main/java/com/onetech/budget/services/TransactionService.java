package com.onetech.budget.services;

import com.onetech.budget.DTO.TransactionRequestDTO;
import com.onetech.budget.models.Client;
import com.onetech.budget.models.Transaction;
import com.onetech.budget.repositories.ClientRepository;
import com.onetech.budget.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final ClientRepository clientRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, ClientRepository clientRepository) {
        this.transactionRepository = transactionRepository;
        this.clientRepository = clientRepository;
    }

    public Transaction saveTransaction(TransactionRequestDTO dto) {
        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new RuntimeException("Client non trouvé avec l'id " + dto.getClientId()));

        Transaction transaction = new Transaction();
        transaction.setMontant(dto.getMontant());
        transaction.setDateOpertation(dto.getDateOpertation());
        transaction.setDateValeur(dto.getDateValeur());
        transaction.setClient(client);

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
