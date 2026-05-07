package com.bincy.banking.transaction.service;

import com.bincy.banking.transaction.dto.TransferRequest;
import com.bincy.banking.transaction.entity.Account;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface AccountService {
    Account createAccount(Account account);

    Account getAccount(String accountNumber);

    List<Account> getAllAccounts();
    void transfer(TransferRequest request);
}
