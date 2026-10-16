package com.bank.account.system.model;

import java.math.BigDecimal;

public enum AccountType {

    CHECKING(0),
    SAVINGS(10);

    private BigDecimal allowedMinimumBalance;

    AccountType(double value) {
        allowedMinimumBalance = new BigDecimal(value);
    }

    public BigDecimal getAllowedMinimumBalance() {
        return allowedMinimumBalance;
    }
}
