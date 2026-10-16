package com.bank.account.system.util;

import com.bank.account.system.model.AccountType;

import java.math.BigDecimal;

public class AccountPropertyValidation {

    public static String checkOwnerNameIsValid(String ownerName) {
        if (ownerName == null) {
            return "Owner name cannot be null";
        }
        if (ownerName.isBlank()) {
            return "Owner name cannot be blank";
        }
        return ownerName;
    }

    public static BigDecimal checkInitialDepositIsValid(AccountType type, BigDecimal initialDeposit) {
        if (initialDeposit == null) {
            throw new IllegalArgumentException("Initial deposit cannot be null");
        }
        if (initialDeposit.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial deposit cannot be negative");
        }
        if (type.equals(AccountType.SAVINGS) && initialDeposit.compareTo(new BigDecimal(10)) < 0) {
            throw new IllegalArgumentException("Initial deposit cannot be less than 10 for SAVINGS account type");
        }
        return initialDeposit;
    }
}
