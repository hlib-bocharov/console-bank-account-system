package com.bank.account.system.test.transfer;

import com.bank.account.system.exception.AccountNotFoundException;
import com.bank.account.system.exception.InvalidAmountException;
import com.bank.account.system.model.Account;
import com.bank.account.system.model.AccountType;
import com.bank.account.system.model.Transaction;
import com.bank.account.system.model.TransactionType;
import com.bank.account.system.test.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DepositTest extends BaseTest {

    @ParameterizedTest
    @DisplayName("Deposit fails with invalid accountNumber parameter")
    @MethodSource("getInvalidAccountNumbers")
    public void testDepositFailsWhenAccountNumberIsInvalid(String accountNumber, String expectedExceptionMessage) {
        var exception = assertThrows(IllegalArgumentException.class, () -> transactionService.deposit(accountNumber, BigDecimal.TEN));
        assertEquals(expectedExceptionMessage, exception.getMessage(), "Exception message is incorrect");
    }

    @Test
    @DisplayName("Get balance fails with account number is missed in the bank")
    public void testDepositFailsWhenAccountNumberIsMissed() {
        var exception = assertThrows(AccountNotFoundException.class, () -> transactionService.deposit("INV111", BigDecimal.TEN));
        assertEquals("Account number INV111 not found.", exception.getMessage());
    }

    @ParameterizedTest
    @DisplayName("Deposit fails with invalid amount parameter")
    @MethodSource("getInvalidAmount")
    public void testDepositFailsWhenAmountIsInvalid(BigDecimal amount, String expectedExceptionMessage) {
        var exception = assertThrows(InvalidAmountException.class, () -> transactionService.deposit("INV111", amount));
        assertEquals(expectedExceptionMessage, exception.getMessage(), "Exception message is incorrect");
    }

    @ParameterizedTest
    @DisplayName("Deposit successfully performs adding amount transaction")
    @ValueSource(strings = {"%s", "   %s", "%s   ", "   %s   "})
    public void testDepositSuccessfullyPerformsAddition(String accountNumberFormat) {
        Account account = accountService.openAccount("John", AccountType.CHECKING, new BigDecimal("2.00"));
        String accountNumber = accountNumberFormat.formatted(account.getAccountNumber());
        bankService.registerAccount(account);
        transactionService.deposit(accountNumber, new BigDecimal("10"));
        Account actualAccount = bankService.findAccount(accountNumber);
        BigDecimal actualBalance= actualAccount.getBalance();
        List<Transaction> actualTransactions = actualAccount.getTransactions();
        assertEquals(new BigDecimal("12.00"), actualBalance, "Account balance after deposit is incorrect");
        assertEquals(1, actualTransactions.size(), "Transactions size is different from 1");
        assertEquals(TransactionType.DEPOSIT, actualTransactions.getFirst().getType(), "Transaction type is not DEPOSIT");
        assertEquals(new BigDecimal("10.00"), actualTransactions.getFirst().getAmount(), "Transaction amount is not 10.00");
        assertEquals(new BigDecimal("12.00"), actualTransactions.getFirst().getResultingBalance(), "Transaction resulting balance is not 12.00");
    }
}
