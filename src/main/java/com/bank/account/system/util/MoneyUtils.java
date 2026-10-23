package com.bank.account.system.util;

import com.bank.account.system.exception.InvalidAmountException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class MoneyUtils {

    private MoneyUtils() {}

    public static BigDecimal getAmountIfValid(BigDecimal amount) {
        if (amount == null) {
            throw new InvalidAmountException("Transaction amount cannot be null.");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Transaction amount must be greater than zero.");
        }
        return amount;
    }

    public static BigDecimal getRoundedAmount(BigDecimal amount) {
        return  amount.setScale(2, RoundingMode.HALF_UP);
    }

    public static String getFormattedAmount(BigDecimal amount) {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        return df.format(amount);
    }
}
