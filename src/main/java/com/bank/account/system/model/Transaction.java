package com.bank.account.system.model;

import java.math.BigDecimal;

public class Transaction {

    private TransactionType type;
    private BigDecimal amount;
    private String timestamp;
    private BigDecimal resultingBalance;

    public Transaction() {
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

    public void setType(TransactionType type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public BigDecimal getResultingBalance() {
        return resultingBalance;
    }

    public void setResultingBalance(BigDecimal resultingBalance) {
        this.resultingBalance = resultingBalance;
    }
}
