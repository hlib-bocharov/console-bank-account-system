package com.bank.account.system.util;

import com.bank.account.system.model.AccountType;

import java.math.BigDecimal;

public class AccountPropertyValidation {

    private AccountPropertyValidation() {}

    public static String checkOwnerNameIsValid(String ownerName) {
        if (ownerName == null) {
            throw new IllegalArgumentException("Owner name cannot be null");
        }
        if (ownerName.isBlank()) {
            throw new IllegalArgumentException("Owner name cannot be blank");
        }
        return ownerName;
    }

    public static BigDecimal getBalanceIfValidForAccountType(AccountType type, BigDecimal balance) {
        if (balance == null) {
            throw new IllegalArgumentException("Balance cannot be null");
        }
        if (balance.compareTo(type.getAllowedMinimumBalance()) < 0) {
            throw new IllegalArgumentException("Balance cannot be less than the allowed minimum balance for %s account with minimum balance %s"
                    .formatted(type, type.getAllowedMinimumBalance()));
        }
        return balance;
    }
}
