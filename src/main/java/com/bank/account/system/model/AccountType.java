package com.bank.account.system.model;

import java.math.BigDecimal;

public enum AccountType {

    CHECKING,
    SAVINGS(10);

    private BigDecimal allowedMinimumBalance;

    AccountType() {
        this(0);
    }

    AccountType(double value) {
        allowedMinimumBalance = new BigDecimal(value).setScale(2);
    }

    public BigDecimal getAllowedMinimumBalance() {
        return allowedMinimumBalance;
    }
}
