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

public class RenameOwnerTest extends BaseTest {

    @ParameterizedTest
    @DisplayName("Rename owner fails with invalid account number")
    @MethodSource("getInvalidAccountNumbers")
    public void testRenameOwnerFailsWhenAccountNumberIsInvalid(String accountNumber, String expectedExceptionMessage) {
        var exception = assertThrows(IllegalArgumentException.class, () -> accountService.renameOwner(accountNumber, "John"));
        assertEquals(expectedExceptionMessage, exception.getMessage(), "Exception message is incorrect");
    }

    @ParameterizedTest
    @DisplayName("Rename owner fails with invalid account number")
    @ValueSource(strings = {"", "   "})
    public void testRenameOwnerFailsWhenAccountNumberIsInvalid(String ownerName) {
        var exception = assertThrows(IllegalArgumentException.class, () -> accountService.renameOwner("ACC001", ownerName));
        assertEquals("Owner name cannot be blank", exception.getMessage(), "Exception message is incorrect");
    }

    @Test
    @DisplayName("Rename owner fails with account number is missed in the bank")
    public void testRenameOwnerFailsWhenAccountNumberIsMissed() {
        Account account = accountService.openAccount("John", AccountType.CHECKING, BigDecimal.TEN);
        bankService.registerAccount(account);
        var exception = assertThrows(AccountNotFoundException.class, () -> accountService.renameOwner("INV111", "Jill"));
        assertEquals("Account number INV111 not found", exception.getMessage());
    }

    @ParameterizedTest
    @DisplayName("Rename owner successfully renames owner name of the account")
    @ValueSource(strings = {"%s", "   %s", "%s   ", "   %s   "})
    public void testRenameOwnerSuccessfullyRemovesExistingAccount(String accountNumberFormat) {
        Account actualAccount = accountService.openAccount("John", AccountType.CHECKING, new BigDecimal("2.00"));
        String accountNumber = actualAccount.getAccountNumber();
        bankService.registerAccount(actualAccount);
        accountService.renameOwner(accountNumberFormat.formatted(accountNumber), "Jill");
        actualAccount =  bankService.findAccount(accountNumber);
        Account expectedAccount = new Account(accountNumber, "Jill", AccountType.CHECKING, new BigDecimal("2.00"));
        verifyEqualityAccountProperties(expectedAccount, actualAccount);
    }

}
