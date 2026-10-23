package com.bank.account.system.test.bank;

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
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FindAccountTest extends BaseTest {

    @ParameterizedTest
    @DisplayName("Find account successfully returns existing account in the bank")
    @ValueSource(strings = {"1234", "   1234", "1234   ", "   1234   "})
    public void testSuccessfullyReturnsExistingAccount(String accountNumber) {
        Account account = new Account("1234", "John", AccountType.CHECKING, BigDecimal.TWO);
        bankService.registerAccount(account);
        Account foundAcc = bankService.findAccount(accountNumber);
        assertEquals(account, foundAcc);
        assertEquals("1234", foundAcc.getAccountNumber());
        assertEquals("John", foundAcc.getOwnerName());
        assertEquals(AccountType.CHECKING, foundAcc.getType());
        assertEquals(new BigDecimal("2.00"), foundAcc.getBalance());
        assertEquals(Collections.emptyList(), foundAcc.getTransactions());
    }

    @ParameterizedTest
    @DisplayName("Find account fails with invalid values")
    @MethodSource("getInvalidAccountNumbers")
    public void testFindAccountFailsWithEmptyAccountNumber(String accountNumber, String expectedMessage) {
        var exception = assertThrows(IllegalArgumentException.class, () -> bankService.findAccount(accountNumber));
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Find account fails with account number is missed in the bank")
    public void testFindAccountFailsWhenAccountNumberIsMissed() {
        var exception = assertThrows(AccountNotFoundException.class, () -> bankService.findAccount("4321"));
        assertEquals("Account number 4321 not found.", exception.getMessage());
    }

}
