package com.bank.account.system.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static com.bank.account.system.util.MoneyUtils.getFormattedAmount;

public class Transaction {

    private final TransactionType type;
    private final BigDecimal amount;
    private final String timestamp;
    private final BigDecimal resultingBalance;

    public Transaction() {
        this(TransactionType.DEPOSIT, BigDecimal.TEN, ZonedDateTime.now().toString(), BigDecimal.TEN);
    }

    public Transaction(TransactionType type, BigDecimal amount, String timestamp, BigDecimal resultingBalance) {
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp;
        this.resultingBalance = resultingBalance;
    }

    public TransactionType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public BigDecimal getResultingBalance() {
        return resultingBalance;
    }

    @Override
    public String toString() {
        return "%s transaction with %s at %s and resulting balance is %s".formatted(type, amount, timestamp, getFormattedAmount(resultingBalance));
    }
}
