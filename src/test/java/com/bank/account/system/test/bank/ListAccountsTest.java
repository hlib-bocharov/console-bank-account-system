package com.bank.account.system.test.bank;

import com.bank.account.system.model.Account;
import com.bank.account.system.model.AccountType;
import com.bank.account.system.test.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ListAccountsTest extends BaseTest {

    @Test
    @DisplayName("Verify that list accounts returns two accounts")
    public void testGetListAccountReturnsListOfTwoAccounts() {
        bankService.registerAccount(new Account("DEF001", "John Doe", AccountType.CHECKING,  BigDecimal.ZERO));
        bankService.registerAccount(new Account("DEF002", "Jill Grin", AccountType.SAVINGS,  BigDecimal.TEN));
        List<Account> actualAccounts = bankService.listAccounts();
        List<Account> expectedAccounts = List.of(
                new Account("DEF001", "John Doe", AccountType.CHECKING,  BigDecimal.ZERO, List.of()),
                new Account("DEF002", "Jill Grin", AccountType.SAVINGS,  BigDecimal.TEN, List.of())
        );
        assertEquals(2,  actualAccounts.size(), "Accounts list should have only two accounts");
        verifyIterableEqualityAccountProperties(expectedAccounts, actualAccounts);
    }

    @Test
    @DisplayName("Verify that list accounts returns one account")
    public void testGetListAccountReturnsListOfOneAccount() {
        bankService.registerAccount(new Account());
        List<Account> actualAccounts = bankService.listAccounts();
        List<Account> expectedAccounts = List.of(
                new Account("DEF001", "John Doe", AccountType.CHECKING,  BigDecimal.ZERO, List.of())
        );
        assertEquals(1,  actualAccounts.size(), "Accounts list should have only one account");
        verifyIterableEqualityAccountProperties(expectedAccounts, actualAccounts);
    }

    @Test
    @DisplayName("Verify that list accounts returns empty account list")
    public void testGetListAccountReturnsEmptyAccountList() {
        List<Account> actualAccounts = bankService.listAccounts();
        assertEquals(0,  actualAccounts.size(), "Accounts list should have no accounts");
        verifyIterableEqualityAccountProperties(Collections.emptyList(), actualAccounts);
    }
}
