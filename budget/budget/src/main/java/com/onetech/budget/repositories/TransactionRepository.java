package com.onetech.budget.repositories;

import com.onetech.budget.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

}
