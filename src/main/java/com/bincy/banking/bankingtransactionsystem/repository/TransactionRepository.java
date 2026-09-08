package com.bincy.banking.bankingtransactionsystem.repository;

import com.bincy.banking.bankingtransactionsystem.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction,Long> {
}
