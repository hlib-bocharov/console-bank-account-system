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

public class WithdrawTest extends BaseTest {

    @ParameterizedTest
    @DisplayName("Withdraw fails with invalid accountNumber parameter")
    @MethodSource("getInvalidAccountNumbers")
    public void testWithdrawFailsWhenAccountNumberIsInvalid(String accountNumber, String expectedExceptionMessage) {
        var exception = assertThrows(IllegalArgumentException.class, () -> transactionService.withdraw(accountNumber, BigDecimal.TEN));
        assertEquals(expectedExceptionMessage, exception.getMessage(), "Exception message is incorrect");
    }

    @Test
    @DisplayName("Withdraw fails with account number is missed in the bank")
    public void testWithdrawFailsWhenAccountNumberIsMissed() {
        var exception = assertThrows(AccountNotFoundException.class, () -> transactionService.withdraw("INV111", BigDecimal.TEN));
        assertEquals("Account number INV111 not found.", exception.getMessage());
    }

    @ParameterizedTest
    @DisplayName("Withdraw fails with invalid amount parameter")
    @MethodSource("getInvalidAmount")
    public void testWithdrawFailsWhenAmountIsInvalid(BigDecimal amount, String expectedExceptionMessage) {
        var exception = assertThrows(InvalidAmountException.class, () -> transactionService.withdraw("INV111", amount));
        assertEquals(expectedExceptionMessage, exception.getMessage(), "Exception message is incorrect");
    }

    @ParameterizedTest
    @DisplayName("Withdraw fails when resulting balance is less than allowed minimum balance")
    @MethodSource("getNegativeParams")
    public void testWithdrawFailsWhenResultingBalanceLessThanAllowed(AccountType accountType, BigDecimal amount) {
        Account account = accountService.openAccount("John", accountType, new BigDecimal("15.00"));
        bankService.registerAccount(account);
        var exception = assertThrows(InsufficientFundsException.class,
                () -> transactionService.withdraw(account.getAccountNumber(), amount));
        assertEquals("Insufficient funds. Requested %s, balance 15.00, short by 1.00.".formatted(amount), exception.getMessage());
        Account actualAccount = bankService.findAccount(account.getAccountNumber());
        BigDecimal actualBalance = actualAccount.getBalance();
        List<Transaction> actualTransactions = actualAccount.getTransactions();
        assertEquals(new BigDecimal("15.00"), actualBalance);
        assertEquals(0, actualTransactions.size());
    }

    @ParameterizedTest
    @DisplayName("Withdraw successfully performs subtracting amount transaction")
    @MethodSource("getPositiveParams")
    public void testWithdrawSuccessfullyPerformsSubtractionAmount(String accountNumberFormat,
                                                                  AccountType accountType,
                                                                  BigDecimal amount,
                                                                  BigDecimal resultingBalance) throws InsufficientFundsException {
        Account account = accountService.openAccount("John", accountType, new BigDecimal("16.00"));
        String accountNumber = accountNumberFormat.formatted(account.getAccountNumber());
        bankService.registerAccount(account);
        transactionService.withdraw(accountNumber, amount);
        Account actualAccount = bankService.findAccount(accountNumber);
        BigDecimal actualBalance = actualAccount.getBalance();
        List<Transaction> actualTransactions = actualAccount.getTransactions();
        assertEquals(resultingBalance, actualBalance, "Account balance after withdraw is incorrect");
        assertEquals(1, actualTransactions.size(), "Transactions size is different from 1");
        assertEquals(TransactionType.WITHDRAW, actualTransactions.getFirst().getType(), "Transaction type is not WITHDRAW");
        assertEquals(amount, actualTransactions.getFirst().getAmount(), "Transaction amount is not " + amount);
        assertEquals(resultingBalance, actualTransactions.getFirst().getResultingBalance(), "Transaction resulting balance is not " + resultingBalance);
    }

    private static Stream<Arguments> getPositiveParams() {
        return Stream.of(
                Arguments.of("%s", AccountType.CHECKING, new BigDecimal("15.00"), new BigDecimal("1.00")),
                Arguments.of("   %s", AccountType.CHECKING, new BigDecimal("16.00"), new BigDecimal("0.00")),
                Arguments.of("%s   ", AccountType.SAVINGS, new BigDecimal("5.00"), new BigDecimal("11.00")),
                Arguments.of("   %s   ", AccountType.SAVINGS, new BigDecimal("6.00"), new BigDecimal("10.00"))
        );
    }

    private static Stream<Arguments> getNegativeParams() {
        return Stream.of(
                Arguments.of(AccountType.CHECKING, new BigDecimal("16.00")),
                Arguments.of(AccountType.SAVINGS, new BigDecimal("6.00"))
        );
    }

}
