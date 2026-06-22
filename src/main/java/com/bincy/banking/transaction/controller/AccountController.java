package com.bincy.banking.transaction.controller;

import com.bincy.banking.transaction.dto.ApiResponse;
import com.bincy.banking.transaction.dto.TransferRequest;
import com.bincy.banking.transaction.entity.Account;
import com.bincy.banking.transaction.service.AccountService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService){
        this.accountService=accountService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Account create(@RequestBody Account account){
       return accountService.createAccount((account));
    }

    @GetMapping("/{accountNumber}")
    public Account get(@PathVariable String accountNumber) {
        return accountService.getAccount(accountNumber);
    }

    @GetMapping
    public List<Account> getAll() {
        return accountService.getAllAccounts();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/transfer")
    public ApiResponse<String> transfer(@RequestBody TransferRequest transferRequest){
        accountService.transfer(transferRequest);
        return ApiResponse.<String>builder()
                .message("Transfer successful")
                .data(null)
                .build();
    }
}
