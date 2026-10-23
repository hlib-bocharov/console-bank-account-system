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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RemoveAccountTest extends BaseTest {

    @ParameterizedTest
    @DisplayName("Remove account successfully removes existing account from the bank")
    @ValueSource(strings = {"1234", "   1234", "1234   ", "   1234   "})
    public void testRemoveAccountSuccessfullyRemovesExistingAccount(String accountNumber) {
        Account account = new Account("1234", "John", AccountType.CHECKING, BigDecimal.TWO);
        bank.getAccounts().put("1234", account);
        bankService.removeAccount(accountNumber);
        assertEquals(0,  bank.getAccounts().size(), "Accounts map should be empty");
    }

    @ParameterizedTest
    @DisplayName("Remove account fails with invalid values")
    @MethodSource("getInvalidAccountNumbers")
    public void testRemoveAccountFailsWithEmptyAccountNumber(String accountNumber, String expectedMessage) {
        var exception = assertThrows(IllegalArgumentException.class, () -> bankService.removeAccount(accountNumber));
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Remove account fails with account number is missed in the bank")
    public void testRemoveAccountFailsWhenAccountNumberIsMissed() {
        var exception = assertThrows(AccountNotFoundException.class, () -> bankService.removeAccount("4321"));
        assertEquals("Account number 4321 not found.", exception.getMessage());
    }

}
