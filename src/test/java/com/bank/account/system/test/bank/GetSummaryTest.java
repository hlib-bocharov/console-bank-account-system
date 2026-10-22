package com.bank.account.system.test.bank;

import com.bank.account.system.model.Account;
import com.bank.account.system.model.AccountType;
import com.bank.account.system.test.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GetSummaryTest extends BaseTest {

    private static final String CORRECT_SUMMARY_PATTERN = "Bank Summary:%n\tname: %s%n\tcode: %s%n\taccount count: %d%n\ttotal holdings: %s%n";

    @Test
    @DisplayName("Get summary returns correctly for 2 accounts in the bank")
    public void testGetSummaryReturnsCorrectlyForTwoAccounts() {
        bankService.registerAccount(new Account("DEF001", "John Doe", AccountType.CHECKING,  BigDecimal.ONE));
        bankService.registerAccount(new Account("DEF002", "Jill Grin", AccountType.SAVINGS,  BigDecimal.TEN));
        String actualSummary = bankService.getSummary();
        String expectedSummary = CORRECT_SUMMARY_PATTERN.formatted(bank.getName(), bank.getCode(), 2, new BigDecimal("11.00"));
        assertEquals(expectedSummary,  actualSummary, "Get summary returns incorrect result");
    }

    @Test
    @DisplayName("Get summary returns correctly for 1 account in the bank")
    public void testGetSummaryReturnsCorrectlyForOneAccount() {
        bankService.registerAccount(new Account("DEF002", "Jill Grin", AccountType.SAVINGS,  BigDecimal.TEN));
        String actualSummary = bankService.getSummary();
        String expectedSummary = CORRECT_SUMMARY_PATTERN.formatted(bank.getName(), bank.getCode(), 1, new BigDecimal("10.00"));
        assertEquals(expectedSummary,  actualSummary, "Get summary returns incorrect result");
    }

    @Test
    @DisplayName("Get summary returns correctly for bank with no account")
    public void testGetSummaryReturnsCorrectlyForNoAccounts() {
        String actualSummary = bankService.getSummary();
        String expectedSummary = CORRECT_SUMMARY_PATTERN.formatted(bank.getName(), bank.getCode(), 0, new BigDecimal("0.00"));
        assertEquals(expectedSummary,  actualSummary, "Get summary returns incorrect result");
    }
}
