package com.bank.account.system.test.transfer;

import com.bank.account.system.model.Account;
import com.bank.account.system.model.AccountType;
import com.bank.account.system.model.Transaction;
import com.bank.account.system.model.TransactionType;
import com.bank.account.system.test.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GetHistoryTest extends BaseTest {

    @Test
    @DisplayName("Get history returns two transactions")
    public void testGetHistoryReturnsListOfTwoTransactions() {
        Account account = accountService.openAccount("John Doe", AccountType.CHECKING, BigDecimal.ZERO);
        bankService.registerAccount(account);
        transactionService.deposit(account.getAccountNumber(), BigDecimal.ONE);
        transactionService.deposit(account.getAccountNumber(), BigDecimal.TWO);
        List<Transaction> actualTransactions = transactionService.getHistory(account.getAccountNumber());
        List<Transaction> expectedTransactions = List.of(
                new Transaction(TransactionType.DEPOSIT, new BigDecimal("2.00"), null, new BigDecimal("3.00")),
                new Transaction(TransactionType.DEPOSIT, new BigDecimal("1.00"), null, new BigDecimal("1.00"))
        );
        assertEquals(2, actualTransactions.size(), "Transaction list should have two items");
        verifyIterableEqualityTransfersIgnoringTimestamp(expectedTransactions, actualTransactions);
    }

    @Test
    @DisplayName("Get history returns one transaction")
    public void testGetHistoryReturnsListOfOneTransaction() {
        Account account = accountService.openAccount("John Doe", AccountType.CHECKING, BigDecimal.ZERO);
        bankService.registerAccount(account);
        transactionService.deposit(account.getAccountNumber(), BigDecimal.ONE);
        List<Transaction> actualTransactions = transactionService.getHistory(account.getAccountNumber());
        List<Transaction> expectedTransactions = List.of(
                new Transaction(TransactionType.DEPOSIT, new BigDecimal("1.00"), null, new BigDecimal("1.00"))
        );
        assertEquals(1, actualTransactions.size(), "Transaction list should have one item");
        verifyIterableEqualityTransfersIgnoringTimestamp(expectedTransactions, actualTransactions);
    }

    @Test
    @DisplayName("Get history returns empty transaction list")
    public void testGetHistoryReturnsEmptyTransactionList() {
        Account account = accountService.openAccount("John Doe", AccountType.CHECKING, BigDecimal.ZERO);
        bankService.registerAccount(account);
        List<Transaction> actualTransactions = transactionService.getHistory(account.getAccountNumber());
        assertEquals(0, actualTransactions.size(), "Transaction list should have no items");
        verifyIterableEqualityTransfersIgnoringTimestamp(List.of(), actualTransactions);
    }
}
