package com.bank.account.system.test.bank;

import com.bank.account.system.model.Account;
import com.bank.account.system.model.AccountType;
import com.bank.account.system.test.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GetAccountCountTest extends BaseTest {

    @Test
    @DisplayName("Get account count returns two when bank has two accounts")
    public void testGetAccountCountReturnsValueTwo() {
        bankService.registerAccount(new Account("DEF001", "John Doe", AccountType.CHECKING,  BigDecimal.ZERO));
        bankService.registerAccount(new Account("DEF002", "Jill Grin", AccountType.SAVINGS,  BigDecimal.TEN));
        int actualCount = bankService.getAccountCount();
        assertEquals(2,  actualCount, "Get account count should return two");
    }

    @Test
    @DisplayName("Get account count returns one when bank has one account")
    public void testGetAccountCountReturnsValueOne() {
        bankService.registerAccount(new Account());
        int actualCount = bankService.getAccountCount();
        assertEquals(1,  actualCount, "Get account count should return one");
    }

    @Test
    @DisplayName("Get account count returns zero when bank has no accounts")
    public void testGetAccountCountReturnsValueZero() {
        int actualCount = bankService.getAccountCount();
        assertEquals(0,  actualCount, "Get account count should return zero");
    }
}
