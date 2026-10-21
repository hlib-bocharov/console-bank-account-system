package com.bank.account.system.test.bank;

import com.bank.account.system.model.Account;
import com.bank.account.system.model.AccountType;
import com.bank.account.system.test.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GetTotalHoldingsTest extends BaseTest {

    @Test
    @DisplayName("Verify that get total holdings returns 11 from two accounts")
    public void testGetTotalHoldingsReturnsValueElevenFromTwoAccounts() {
        bankService.registerAccount(new Account("DEF001", "John Doe", AccountType.CHECKING,  BigDecimal.ONE));
        bankService.registerAccount(new Account("DEF002", "Jill Grin", AccountType.SAVINGS,  BigDecimal.TEN));
        BigDecimal actualHoldings = bankService.getTotalHoldings();
        assertEquals(new BigDecimal("11.00"),  actualHoldings, "Get total holdings should return 11.00");
    }

    @Test
    @DisplayName("Verify that get total holdings returns 10 from one account")
    public void testGetTotalHoldingsReturnsValueTenFromOneAccounts() {
        bankService.registerAccount(new Account("DEF002", "Jill Grin", AccountType.SAVINGS,  BigDecimal.TEN));
        BigDecimal actualHoldings = bankService.getTotalHoldings();
        assertEquals(new BigDecimal("10.00"),  actualHoldings, "Get total holdings should return 10.00");
    }

    @Test
    @DisplayName("Verify that get total holdings returns 0 when bank has no accounts")
    public void testGetTotalHoldingsReturnsValueZeroWhenBankHasNoAccounts() {
        BigDecimal actualHoldings = bankService.getTotalHoldings();
        assertEquals(new BigDecimal("0.00"),  actualHoldings, "Get total holdings should return 0.00");
    }
}
