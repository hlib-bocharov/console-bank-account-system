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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GetBalanceTest extends BaseTest {

    @ParameterizedTest
    @DisplayName("Get balance fails with invalid values")
    @MethodSource("getInvalidAccountNumbers")
    public void testGetBalanceFailsWhenAccountNumberIsInvalid(String accountNumber, String expectedExceptionMessage) {
        var exception = assertThrows(IllegalArgumentException.class, () -> accountService.getBalance(accountNumber));
        assertEquals(expectedExceptionMessage, exception.getMessage(), "Exception message is incorrect");
    }

    @Test
    @DisplayName("Get balance fails with account number is missed in the bank")
    public void testGetBalanceFailsWhenAccountNumberIsMissed() {
        var exception = assertThrows(AccountNotFoundException.class, () -> accountService.getBalance("INV111"));
        assertEquals("Account number INV111 not found.", exception.getMessage());
    }

    @ParameterizedTest
    @DisplayName("Get balance successfully retrieve account balance")
    @ValueSource(strings = {"%s", "   %s", "%s   ", "   %s   "})
    public void testGetBalanceSuccessfullyRemovesExistingAccount(String accountNumberFormat) {
        Account account = accountService.openAccount("John", AccountType.CHECKING, new BigDecimal("2.00"));
        bankService.registerAccount(account);
        BigDecimal actualBalance = accountService.getBalance(accountNumberFormat.formatted(account.getAccountNumber()));
        assertEquals(new BigDecimal("2.00"), actualBalance, "Actual returned account balance is not as expected");
    }

}
