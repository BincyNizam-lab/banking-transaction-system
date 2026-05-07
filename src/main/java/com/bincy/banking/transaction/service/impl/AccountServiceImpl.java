package com.bincy.banking.transaction.service.impl;

import com.bincy.banking.transaction.dto.TransferRequest;
import com.bincy.banking.transaction.entity.Account;
import com.bincy.banking.transaction.entity.Transaction;
import com.bincy.banking.transaction.repository.AccountRepository;
import com.bincy.banking.transaction.repository.TransactionRepository;
import com.bincy.banking.transaction.service.AccountService;
import jakarta.transaction.Transactional;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository repository;
    private final TransactionRepository transactionRepository;

    public AccountServiceImpl(AccountRepository repository, TransactionRepository transactionRepository){
        this.repository=repository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Account createAccount(Account account) {
        return repository.save(account);
    }

    @Override
    public Account getAccount(String accountNumber) {
        return repository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    @Override
    public List<Account> getAllAccounts() {
        return repository.findAll();
    }
    @Override
    @Transactional
    public void transfer(TransferRequest request){
        try {
            Account from = repository.findByAccountNumber(request.getFromAccount())
                    .orElseThrow(() -> new RuntimeException("From account not found"));

            Account to = repository.findByAccountNumber(request.getToAccount())
                    .orElseThrow(() -> new RuntimeException("To account not found"));

            if (from.getBalance().compareTo(request.getAmount()) < 0) {
                throw new RuntimeException("Insufficient balance");
            }

            // Deduct
            from.setBalance(from.getBalance().subtract(request.getAmount()));

            // Add
            to.setBalance(to.getBalance().add(request.getAmount()));

            repository.save(from);
            repository.save(to);

            // Save transaction
            Transaction txn = Transaction.builder()
                    .fromAccount(from.getAccountNumber())
                    .toAccount(to.getAccountNumber())
                    .amount(request.getAmount())
                    .status("SUCCESS")
                    .build();


            transactionRepository.save(txn);

    } catch (ObjectOptimisticLockingFailureException ex) {
        throw new RuntimeException("Transaction failed due to concurrent update. Please retry.");
    }
    }
}
