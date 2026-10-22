package com.bank.account.system.test;

import com.bank.account.system.model.Account;
import com.bank.account.system.model.Bank;
import com.bank.account.system.service.AccountService;
import com.bank.account.system.service.BankService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.provider.Arguments;
import org.opentest4j.AssertionFailedError;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BaseTest {

    protected Bank bank;
    protected BankService bankService;
    protected AccountService accountService;

    @BeforeEach
    protected void setupBank() {
        bank = new Bank("Test Bank", "TB001");
        bankService = new BankService(bank);
        accountService = new AccountService(bankService);
    }

    protected void verifyEqualityAccountProperties(Account expectedAccount, Account actualAccount, String... prefixes) {
        String prefix = (prefixes.length == 0 || prefixes[0] == null) ? "" : prefixes[0];
        assertTrue(actualAccount.getAccountNumber().matches("\\w{3}\\d{3}"),
                prefix + "accountNumber [%s] does not match pattern (%s)".formatted(actualAccount.getAccountNumber(), "\\w{3}\\d{3}"));
        assertEquals(expectedAccount.getOwnerName(), actualAccount.getOwnerName(),
                accountPropertyMismatchMessage("ownerName", expectedAccount.getOwnerName(), actualAccount.getOwnerName(), prefix));
        assertEquals(expectedAccount.getType(), actualAccount.getType(),
                accountPropertyMismatchMessage("type", expectedAccount.getType(), actualAccount.getType(), prefix));
        assertEquals(expectedAccount.getBalance(), actualAccount.getBalance(),
                accountPropertyMismatchMessage("balance", expectedAccount.getBalance(), actualAccount.getBalance(), prefix));
    }

    protected void verifyIterableEqualityAccountProperties(List<Account> expectedAccounts, List<Account> actualAccounts) {
        if (actualAccounts.size() != expectedAccounts.size()) {
            throw new AssertionFailedError("Collections have different number of records", expectedAccounts.size(), actualAccounts.size());
        }
        List<Account> sortedAccounts = new ArrayList<>(actualAccounts);
        sortedAccounts.sort(Comparator.comparing(Account::getAccountNumber));
        Iterator<Account> actualIterator = sortedAccounts.iterator();
        Iterator<Account> expectedIterator = expectedAccounts.iterator();
        int index = -1;
        while (actualIterator.hasNext() && expectedIterator.hasNext()) {
            Account actualAccount = actualIterator.next();
            Account expectedAccount = expectedIterator.next();
            index++;
            verifyEqualityAccountProperties(expectedAccount, actualAccount, "Accounts[" + index + "].");
        }
    }

    private <T> String accountPropertyMismatchMessage(String propertyName, T expectedValue, T actualValue, String prefix) {
        return "%s%s mismatch: expected [%s], actual [%s]".formatted(prefix, propertyName, expectedValue, actualValue);
    }

    protected static Stream<Arguments> getInvalidAccountNumbers() {
        return Stream.of(
                Arguments.of(null, "Account number must be not null"),
                Arguments.of("", "Account number must be not blank"),
                Arguments.of("  ", "Account number must be not blank")
        );
    }
}
