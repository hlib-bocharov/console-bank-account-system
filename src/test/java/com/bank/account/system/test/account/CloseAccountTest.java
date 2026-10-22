package com.bank.account.system.test.account;

import com.bank.account.system.exception.AccountNotFoundException;
import com.bank.account.system.model.Account;
import com.bank.account.system.model.AccountType;
import com.bank.account.system.test.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class CloseAccountTest extends BaseTest {

    @ParameterizedTest
    @DisplayName("Close account fails with invalid values")
    @MethodSource("getInvalidAccountNumbers")
    public void testCloseAccountFailsWhenAccountNumberIsInvalid(String accountNumber, String expectedExceptionMessage) {
        var exception = assertThrows(IllegalArgumentException.class, () -> accountService.closeAccount(accountNumber));
        assertEquals(expectedExceptionMessage, exception.getMessage(), "Exception message is incorrect");
    }

    @Test
    @DisplayName("Close account fails with account number is missed in the bank")
    public void testCloseAccountFailsWhenAccountNumberIsMissed() {
        var exception = assertThrows(AccountNotFoundException.class, () -> accountService.closeAccount("INV111"));
        assertEquals("Account number INV111 not found", exception.getMessage());
    }

    @Test
    @DisplayName("Close account skips with non-zero account balance")
    public void testCloseAccountSkipsWhenBalanceIsNotZero() {
        Account account = accountService.openAccount("John", AccountType.CHECKING, BigDecimal.ONE);
        bankService.registerAccount(account);
        accountService.closeAccount(account.getAccountNumber());
        assertNotNull(bankService.findAccount(account.getAccountNumber()),
                "Account was not found after closure attempt but should be present");
        assertEquals(1, bankService.getAccountCount(),
                "There are no accounts after closure attempt but one should be");
    }

    @ParameterizedTest
    @DisplayName("Close account successfully removes existing account from the bank if balance is zero")
    @ValueSource(strings = {"%s", "   %s", "%s   ", "   %s   "})
    public void testCloseAccountSuccessfullyRemovesExistingAccount(String accountNumberFormat) {
        Account account = accountService.openAccount("John", AccountType.CHECKING, new BigDecimal("0.00"));
        bankService.registerAccount(account);
        accountService.closeAccount(accountNumberFormat.formatted(account.getAccountNumber()));
        assertEquals(0, bankService.listAccounts().size(),
                "Account list is not empty after account closure");
    }
}
