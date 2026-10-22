package com.bank.account.system.test.bank;

import com.bank.account.system.model.Account;
import com.bank.account.system.model.AccountType;
import com.bank.account.system.model.Bank;
import com.bank.account.system.service.BankService;
import com.bank.account.system.test.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterAccountTest extends BaseTest {

    @Test
    @Tag("Negative")
    @DisplayName("Register Account fails when account is null")
    public void testRegisterAccountFailsIfAccountIsNull() {
        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> bankService.registerAccount(null),
                "IllegalArgumentException should have been thrown");
        assertEquals("Account must be not null", exception.getMessage(), "Exception message is incorrect");
        assertEquals(0, bank.getAccounts().size(), "Accounts map should be empty");
    }

    @Test
    @Tag("Negative")
    @DisplayName("Register Account fails when account has null account number")
    public void testRegisterAccountFailsIfAccountContainsNullAccountNumber() {
        Account account = new Account(null, "John", AccountType.CHECKING, BigDecimal.ONE);
        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> bankService.registerAccount(account),
                "IllegalArgumentException should have been thrown");
        assertEquals("Account number must be not null", exception.getMessage(), "Exception message is incorrect");
        assertEquals(0, bank.getAccounts().size(), "Accounts map should be empty");
    }

    @Test
    @Tag("Negative")
    @DisplayName("Register Account doesn't add already existed account")
    public void testRegisterAccountIgnoresToAddAlreadyExistedAccount() {
        Account account = new Account("1234", "John",  AccountType.CHECKING, BigDecimal.ONE);
        bank = new Bank("Test Bank", "TB001", new HashMap<>(Map.of("1234", account)));
        bankService = new BankService(bank);
        bankService.registerAccount(account);
        assertEquals(1, bank.getAccounts().size(), "Accounts map should have only one account");
        assertEquals(Map.of("1234", account), bank.getAccounts());
    }

    @Test
    @Tag("Negative")
    @DisplayName("Register Account doesn't add account with account number which exists in the bank")
    public void testRegisterAccountIgnoresToAddAccountWithExistingAccountNumberInTheBank() {
        Account account1 = new Account("1234", "John",  AccountType.CHECKING, BigDecimal.ONE);
        bank = new Bank("Test Bank", "TB001", new HashMap<>(Map.of("1234", account1)));
        bankService = new BankService(bank);
        Account account2 = new Account("1234", "Jill",  AccountType.CHECKING, BigDecimal.ONE);
        bankService.registerAccount(account2);
        assertEquals(1, bank.getAccounts().size(), "Accounts map should have only one account");
        assertEquals(Map.of("1234", account1), bank.getAccounts());
    }

    @Test
    @Tag("Positive")
    @DisplayName("Register Account successfully adds account to the bank")
    public void testRegisterAccountSuccessfullyAddsAccountToTheBank() {
        Bank bank = new Bank("Test Bank", "TB001");
        BankService bankService = new BankService(bank);
        Account account = new Account("1234", "John",  AccountType.CHECKING, BigDecimal.ONE);
        bankService.registerAccount(account);
        assertEquals(1, bank.getAccounts().size(), "Accounts map should have only one account");
        assertEquals(Map.of("1234", account), bank.getAccounts());
    }
}
