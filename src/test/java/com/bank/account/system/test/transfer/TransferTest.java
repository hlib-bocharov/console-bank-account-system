package com.bank.account.system.test.transfer;

import com.bank.account.system.exception.AccountNotFoundException;
import com.bank.account.system.exception.InsufficientFundsException;
import com.bank.account.system.exception.InvalidAmountException;
import com.bank.account.system.model.Account;
import com.bank.account.system.model.AccountType;
import com.bank.account.system.model.Transaction;
import com.bank.account.system.model.TransactionType;
import com.bank.account.system.test.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TransferTest extends BaseTest {

    @ParameterizedTest
    @DisplayName("Transfer fails with invalid fromAccount parameter")
    @MethodSource("getInvalidAccountNumbers")
    public void testTransferFailsWhenFromAccountIsInvalid(String accountNumber, String expectedExceptionMessage) {
        Account account = accountService.openAccount("John", AccountType.CHECKING, BigDecimal.TEN);
        bankService.registerAccount(account);
        var exception = assertThrows(IllegalArgumentException.class, () -> transactionService.transfer(accountNumber, account.getAccountNumber(), BigDecimal.TEN));
        assertEquals(expectedExceptionMessage, exception.getMessage(), "Exception message is incorrect");
        Account toAccount = bankService.findAccount(account.getAccountNumber());
        assertEquals(new BigDecimal("10.00"), toAccount.getBalance(), "Balance in toAccount was changed");
        assertEquals(List.of(), toAccount.getTransactions(), "Transactions in toAccount is not empty");

    }

    @ParameterizedTest
    @DisplayName("Transfer fails with invalid toAccount parameter")
    @MethodSource("getInvalidAccountNumbers")
    public void testTransferFailsWhenToAccountIsInvalid(String accountNumber, String expectedExceptionMessage) {
        Account account = accountService.openAccount("John", AccountType.CHECKING, BigDecimal.TEN);
        bankService.registerAccount(account);
        var exception = assertThrows(IllegalArgumentException.class, () -> transactionService.transfer(account.getAccountNumber(), accountNumber, BigDecimal.TEN));
        assertEquals(expectedExceptionMessage, exception.getMessage(), "Exception message is incorrect");
        Account fromAccount = bankService.findAccount(account.getAccountNumber());
        assertEquals(new BigDecimal("10.00"), fromAccount.getBalance(), "Balance in toAccount was changed");
        assertEquals(List.of(), fromAccount.getTransactions(), "Transactions in toAccount is not empty");
    }

    @ParameterizedTest
    @DisplayName("Transfer fails with invalid amount parameter")
    @MethodSource("getInvalidAmount")
    public void testTransferFailsWhenAmountIsInvalid(BigDecimal amount, String expectedExceptionMessage) {
        Account account1 = accountService.openAccount("John", AccountType.CHECKING, BigDecimal.TEN);
        bankService.registerAccount(account1);
        Account account2 = accountService.openAccount("John", AccountType.CHECKING, BigDecimal.TEN);
        bankService.registerAccount(account2);
        var exception = assertThrows(InvalidAmountException.class, () -> transactionService.transfer(account1.getAccountNumber(), account2.getAccountNumber(), amount));
        assertEquals(expectedExceptionMessage, exception.getMessage(), "Exception message is incorrect");
        Account fromAccount = bankService.findAccount(account1.getAccountNumber());
        Account toAccount = bankService.findAccount(account2.getAccountNumber());
        assertEquals(new BigDecimal("10.00"), fromAccount.getBalance(), "Balance in toAccount was changed");
        assertEquals(List.of(), fromAccount.getTransactions(), "Transactions in toAccount is not empty");
        assertEquals(new BigDecimal("10.00"), toAccount.getBalance(), "Balance in toAccount was changed");
        assertEquals(List.of(), toAccount.getTransactions(), "Transactions in toAccount is not empty");
    }

    @Test
    @DisplayName("Transfer fails with fromAccount is missed in the bank")
    public void testTransferFailsWhenFromAccountIsMissed() {
        Account account2 = accountService.openAccount("John", AccountType.CHECKING, BigDecimal.TEN);
        bankService.registerAccount(account2);
        var exception = assertThrows(AccountNotFoundException.class, () -> transactionService.transfer("INV111", account2.getAccountNumber(), BigDecimal.TEN));
        assertEquals("Account number INV111 not found.", exception.getMessage());
        Account toAccount = bankService.findAccount(account2.getAccountNumber());
        assertEquals(new BigDecimal("10.00"), toAccount.getBalance(), "Balance in toAccount was changed");
        assertEquals(List.of(), toAccount.getTransactions(), "Transactions in toAccount is not empty");
    }

    @ParameterizedTest
    @DisplayName("Transfer fails when resulting balance is less than allowed minimum balance in fromAccount")
    @MethodSource("getNegativeParams")
    public void testTransferFailsWhenResultingBalanceLessThanAllowedForFromAccount(AccountType accountType, BigDecimal amount) {
        Account account1 = accountService.openAccount("John", accountType, new BigDecimal("15.00"));
        bankService.registerAccount(account1);
        Account account2 = accountService.openAccount("John", AccountType.CHECKING, BigDecimal.TEN);
        bankService.registerAccount(account2);
        var exception = assertThrows(InsufficientFundsException.class,
                () -> transactionService.transfer(account1.getAccountNumber(), account2.getAccountNumber(), amount));
        assertEquals("Insufficient funds. Requested %s, balance 15.00, short by 1.00.".formatted(amount), exception.getMessage());
        Account fromAccount = bankService.findAccount(account1.getAccountNumber());
        Account toAccount = bankService.findAccount(account2.getAccountNumber());
        assertEquals(new BigDecimal("15.00"), fromAccount.getBalance(), "Balance in toAccount was changed");
        assertEquals(List.of(), fromAccount.getTransactions(), "Transactions in toAccount is not empty");
        assertEquals(new BigDecimal("10.00"), toAccount.getBalance(), "Balance in toAccount was changed");
        assertEquals(List.of(), toAccount.getTransactions(), "Transactions in toAccount is not empty");
    }

    @Test
    @DisplayName("Transfer fails with toAccount is missed in the bank")
    public void testTransferFailsWhenToAccountIsMissed() {
        Account account1 = accountService.openAccount("John", AccountType.CHECKING, new BigDecimal("15.00"));
        bankService.registerAccount(account1);
        var exception = assertThrows(AccountNotFoundException.class, () -> transactionService.transfer(account1.getAccountNumber(), "INV111", BigDecimal.TEN));
        assertEquals("Account number INV111 not found.", exception.getMessage());
        Account fromAccount = bankService.findAccount(account1.getAccountNumber());
        assertEquals(new BigDecimal("15.00"), fromAccount.getBalance(), "Balance in toAccount was changed");
        assertEquals(List.of(), fromAccount.getTransactions(), "Transactions in toAccount is not empty");
    }

    @ParameterizedTest
    @DisplayName("Transfer successfully performs transaction")
    @MethodSource("getPositiveParams")
    public void testTransferSuccessfullyPerformsAmountTransaction(String accountNumberFormat,
                                                                  AccountType accountType,
                                                                  BigDecimal amount,
                                                                  BigDecimal fromResultingBalance,
                                                                  BigDecimal toResultingBalance) throws InsufficientFundsException {
        Account account1 = accountService.openAccount("John", accountType, new BigDecimal("16.00"));
        String accountNumber1 = accountNumberFormat.formatted(account1.getAccountNumber());
        bankService.registerAccount(account1);
        Account account2 = accountService.openAccount("John", accountType, new BigDecimal("16.00"));
        String accountNumber2 = accountNumberFormat.formatted(account2.getAccountNumber());
        bankService.registerAccount(account2);
        transactionService.transfer(accountNumber1, accountNumber2, amount);
        Account fromAccount = bankService.findAccount(accountNumber1);
        List<Transaction> actualTransactions = fromAccount.getTransactions();
        assertEquals(fromResultingBalance, fromAccount.getBalance(), "Account balance after transfer is incorrect");
        assertEquals(1, actualTransactions.size(), "Transactions size is different from 1");
        assertEquals(TransactionType.TRANSFER, actualTransactions.getFirst().getType(), "Transaction type is not TRANSFER");
        assertEquals(amount, actualTransactions.getFirst().getAmount(), "Transaction amount is not " + amount);
        assertEquals(fromResultingBalance, actualTransactions.getFirst().getResultingBalance(), "Transaction resulting balance is not " + fromResultingBalance);
        Account toAccount = bankService.findAccount(accountNumber2);
        List<Transaction> toTransactions = toAccount.getTransactions();
        assertEquals(toResultingBalance, toAccount.getBalance(), "Account balance after transfer is incorrect");
        assertEquals(1, toTransactions.size(), "Transactions size is different from 1");
        assertEquals(TransactionType.TRANSFER, toTransactions.getFirst().getType(), "Transaction type is not TRANSFER");
        assertEquals(amount, toTransactions.getFirst().getAmount(), "Transaction amount is not " + amount);
        assertEquals(toResultingBalance, toTransactions.getFirst().getResultingBalance(), "Transaction resulting balance is not " + toResultingBalance);
    }

    private static Stream<Arguments> getPositiveParams() {
        return Stream.of(
                Arguments.of("%s", AccountType.CHECKING, new BigDecimal("15.00"), new BigDecimal("1.00"), new BigDecimal("31.00")),
                Arguments.of("   %s", AccountType.CHECKING, new BigDecimal("16.00"), new BigDecimal("0.00"), new BigDecimal("32.00")),
                Arguments.of("%s   ", AccountType.SAVINGS, new BigDecimal("5.00"), new BigDecimal("11.00"), new BigDecimal("21.00")),
                Arguments.of("   %s   ", AccountType.SAVINGS, new BigDecimal("6.00"), new BigDecimal("10.00"), new BigDecimal("22.00"))
        );
    }

    private static Stream<Arguments> getNegativeParams() {
        return Stream.of(
                Arguments.of(AccountType.CHECKING, new BigDecimal("16.00")),
                Arguments.of(AccountType.SAVINGS, new BigDecimal("6.00"))
        );
    }
}
