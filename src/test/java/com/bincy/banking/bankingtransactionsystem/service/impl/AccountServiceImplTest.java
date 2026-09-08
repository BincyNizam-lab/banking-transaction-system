package com.bincy.banking.bankingtransactionsystem.service.impl;

import com.bincy.banking.bankingtransactionsystem.dto.TransferRequest;
import com.bincy.banking.bankingtransactionsystem.entity.Account;
import com.bincy.banking.bankingtransactionsystem.entity.Transaction;
import com.bincy.banking.bankingtransactionsystem.repository.AccountRepository;
import com.bincy.banking.bankingtransactionsystem.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Comprehensive test suite for AccountServiceImpl using JUnit 5 and Mockito.
 * Tests cover account creation, retrieval, and transfer operations.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AccountServiceImpl Tests")
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    private Account fromAccount;
    private Account toAccount;

    @BeforeEach
    void setUp() {
        fromAccount = Account.builder()
                .id(1L)
                .accountNumber("ACC001")
                .holderName("John Doe")
                .balance(new BigDecimal("5000.00"))
                .createdAt(LocalDateTime.now())
                .version(1L)
                .build();

        toAccount = Account.builder()
                .id(2L)
                .accountNumber("ACC002")
                .holderName("Jane Doe")
                .balance(new BigDecimal("3000.00"))
                .createdAt(LocalDateTime.now())
                .version(1L)
                .build();
    }

    @Test
    @DisplayName("Should create account successfully")
    void testCreateAccount_Success() {
        when(accountRepository.save(any(Account.class))).thenReturn(fromAccount);

        Account result = accountService.createAccount(fromAccount);

        assertNotNull(result);
        assertEquals("ACC001", result.getAccountNumber());
        assertEquals("John Doe", result.getHolderName());
        assertEquals(new BigDecimal("5000.00"), result.getBalance());
        verify(accountRepository, times(1)).save(fromAccount);
    }

    @Test
    @DisplayName("Should create account with any balance")
    void testCreateAccount_VariousBalances() {
        Account acc = Account.builder()
                .accountNumber("ACC999")
                .holderName("Test User")
                .balance(BigDecimal.ZERO)
                .build();

        when(accountRepository.save(any(Account.class))).thenReturn(acc);

        Account result = accountService.createAccount(acc);

        assertEquals(0, result.getBalance().compareTo(BigDecimal.ZERO));
        verify(accountRepository).save(acc);
    }

    @Test
    @DisplayName("Should get account by account number successfully")
    void testGetAccount_Success() {
        when(accountRepository.findByAccountNumber("ACC001")).thenReturn(Optional.of(fromAccount));

        Account result = accountService.getAccount("ACC001");

        assertNotNull(result);
        assertEquals("ACC001", result.getAccountNumber());
        assertEquals("John Doe", result.getHolderName());
        verify(accountRepository, times(1)).findByAccountNumber("ACC001");
    }

    @Test
    @DisplayName("Should throw RuntimeException when account not found")
    void testGetAccount_NotFound() {
        when(accountRepository.findByAccountNumber("INVALID")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> accountService.getAccount("INVALID"));

        assertEquals("Account not found", exception.getMessage());
        verify(accountRepository, times(1)).findByAccountNumber("INVALID");
    }

    @Test
    @DisplayName("Should get all accounts successfully")
    void testGetAllAccounts_Success() {
        List<Account> accountList = List.of(fromAccount, toAccount);
        when(accountRepository.findAll()).thenReturn(accountList);

        List<Account> result = accountService.getAllAccounts();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("ACC001", result.get(0).getAccountNumber());
        assertEquals("ACC002", result.get(1).getAccountNumber());
        verify(accountRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no accounts exist")
    void testGetAllAccounts_Empty() {
        when(accountRepository.findAll()).thenReturn(List.of());

        List<Account> result = accountService.getAllAccounts();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(accountRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should transfer successfully between accounts")
    void testTransfer_Success() {
        TransferRequest request = new TransferRequest();
        request.setFromAccount("ACC001");
        request.setToAccount("ACC002");
        request.setAmount(new BigDecimal("500.00"));

        when(accountRepository.findByAccountNumber("ACC001")).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber("ACC002")).thenReturn(Optional.of(toAccount));
        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        accountService.transfer(request);

        assertEquals(new BigDecimal("4500.00"), fromAccount.getBalance());
        assertEquals(new BigDecimal("3500.00"), toAccount.getBalance());

        verify(accountRepository).findByAccountNumber("ACC001");
        verify(accountRepository).findByAccountNumber("ACC002");
        verify(accountRepository, times(2)).save(any(Account.class));

        ArgumentCaptor<Transaction> txnCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(txnCaptor.capture());
        Transaction savedTxn = txnCaptor.getValue();
        assertEquals("ACC001", savedTxn.getFromAccount());
        assertEquals("ACC002", savedTxn.getToAccount());
        assertEquals(new BigDecimal("500.00"), savedTxn.getAmount());
        assertEquals("SUCCESS", savedTxn.getStatus());
    }

    @Test
    @DisplayName("Should fail transfer when source account not found")
    void testTransfer_SourceAccountNotFound() {
        TransferRequest request = new TransferRequest();
        request.setFromAccount("INVALID");
        request.setToAccount("ACC002");
        request.setAmount(new BigDecimal("500.00"));

        when(accountRepository.findByAccountNumber("INVALID")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> accountService.transfer(request));


        assertEquals("From account not found", exception.getMessage());
        verify(accountRepository).findByAccountNumber("INVALID");
        verify(accountRepository, never()).findByAccountNumber("ACC002");
        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should fail transfer when destination account not found")
    void testTransfer_DestinationAccountNotFound() {
        TransferRequest request = new TransferRequest();
        request.setFromAccount("ACC001");
        request.setToAccount("INVALID");
        request.setAmount(new BigDecimal("500.00"));

        when(accountRepository.findByAccountNumber("ACC001")).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber("INVALID")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> accountService.transfer(request));

        assertEquals("To account not found", exception.getMessage());
        verify(accountRepository).findByAccountNumber("ACC001");
        verify(accountRepository).findByAccountNumber("INVALID");
        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should fail transfer when insufficient balance")
    void testTransfer_InsufficientBalance() {
        TransferRequest request = new TransferRequest();
        request.setFromAccount("ACC001");
        request.setToAccount("ACC002");
        request.setAmount(new BigDecimal("10000.00"));

        when(accountRepository.findByAccountNumber("ACC001")).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber("ACC002")).thenReturn(Optional.of(toAccount));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> accountService.transfer(request));


        assertEquals("Insufficient balance", exception.getMessage());
        verify(accountRepository).findByAccountNumber("ACC001");
        verify(accountRepository).findByAccountNumber("ACC002");
        verify(accountRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should handle optimistic locking failure")
    void testTransfer_OptimisticLockingFailure() {
        TransferRequest request = new TransferRequest();
        request.setFromAccount("ACC001");
        request.setToAccount("ACC002");
        request.setAmount(new BigDecimal("500.00"));

        when(accountRepository.findByAccountNumber("ACC001")).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber("ACC002")).thenReturn(Optional.of(toAccount));
        when(accountRepository.save(any(Account.class)))
                .thenThrow(new ObjectOptimisticLockingFailureException("Concurrent update", null));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> accountService.transfer(request));


        assertEquals("Transaction failed due to concurrent update. Please retry.", exception.getMessage());
        verify(accountRepository).findByAccountNumber("ACC001");
        verify(accountRepository).findByAccountNumber("ACC002");
        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should transfer zero amount")
    void testTransfer_ZeroAmount() {
        TransferRequest request = new TransferRequest();
        request.setFromAccount("ACC001");
        request.setToAccount("ACC002");
        request.setAmount(BigDecimal.ZERO);

        when(accountRepository.findByAccountNumber("ACC001")).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber("ACC002")).thenReturn(Optional.of(toAccount));
        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        accountService.transfer(request);

        assertEquals(new BigDecimal("5000.00"), fromAccount.getBalance());
        assertEquals(new BigDecimal("3000.00"), toAccount.getBalance());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should transfer to same account holder")
    void testTransfer_SameAccountHolder() {
        Account accountCopy = Account.builder()
                .id(3L)
                .accountNumber("ACC003")
                .holderName("John Doe")
                .balance(new BigDecimal("2000.00"))
                .createdAt(LocalDateTime.now())
                .version(1L)
                .build();

        TransferRequest request = new TransferRequest();
        request.setFromAccount("ACC001");
        request.setToAccount("ACC003");
        request.setAmount(new BigDecimal("500.00"));

        when(accountRepository.findByAccountNumber("ACC001")).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber("ACC003")).thenReturn(Optional.of(accountCopy));
        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        accountService.transfer(request);

        assertEquals(new BigDecimal("4500.00"), fromAccount.getBalance());
        assertEquals(new BigDecimal("2500.00"), accountCopy.getBalance());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should handle transfer with exact balance")
    void testTransfer_ExactBalance() {
        Account acc = Account.builder()
                .id(4L)
                .accountNumber("ACC004")
                .holderName("Balance Test")
                .balance(new BigDecimal("500.00"))
                .version(1L)
                .build();

        TransferRequest request = new TransferRequest();
        request.setFromAccount("ACC004");
        request.setToAccount("ACC002");
        request.setAmount(new BigDecimal("500.00"));

        when(accountRepository.findByAccountNumber("ACC004")).thenReturn(Optional.of(acc));
        when(accountRepository.findByAccountNumber("ACC002")).thenReturn(Optional.of(toAccount));
        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        accountService.transfer(request);

        assertEquals(0, acc.getBalance().compareTo(BigDecimal.ZERO));
        assertEquals(new BigDecimal("3500.00"), toAccount.getBalance());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should create account with null fields")
    void testCreateAccount_WithNullFields() {
        Account acc = Account.builder()
                .accountNumber("ACC100")
                .holderName("Test")
                .build();

        when(accountRepository.save(any(Account.class))).thenReturn(acc);

        Account result = accountService.createAccount(acc);

        assertNull(result.getBalance());
        verify(accountRepository).save(acc);
    }
}
