package com.bank.account.system.util;

import com.bank.account.system.exception.InvalidAmountException;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TransactionPropertyValidation {

    public static BigDecimal checkAmountIsValid(BigDecimal amount) {
        if (amount == null) {
            throw new InvalidAmountException("Transaction amount cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Transaction amount must be greater than zero");
        }
        return amount.setScale(2, RoundingMode.HALF_UP);
    }
}
