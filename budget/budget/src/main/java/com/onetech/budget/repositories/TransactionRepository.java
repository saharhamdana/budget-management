package com.onetech.budget.repositories;

import com.onetech.budget.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByReference(String reference);
    boolean existsByReference(String reference);
    boolean existsByReferenceAndClient(String reference, String client);
    List<Transaction> findByClient(String client);
}