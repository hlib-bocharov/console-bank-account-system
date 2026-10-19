package com.bank.account.system.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static com.bank.account.system.util.AccountNumberGenerator.nextDefault;
import static com.bank.account.system.util.MoneyUtils.getFormattedAmount;

public class Account {

    private final String accountNumber;
    private String ownerName;
    private final AccountType type;
    private BigDecimal balance;
    private List<Transaction> transactions;

    public Account() {
        this(nextDefault(), "John Doe", AccountType.CHECKING,  BigDecimal.ZERO);
    }

    public Account(String accountNumber, String ownerName, AccountType type, BigDecimal balance) {
        this(accountNumber, ownerName, type, balance, new LinkedList<>());
    }

    public Account(String accountNumber, String ownerName, AccountType type, BigDecimal balance, List<Transaction> transactions) {
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.type = type;
        this.balance = balance.setScale(2, RoundingMode.HALF_UP);
        this.transactions = transactions;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public AccountType getType() {
        return type;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public List<Transaction> getTransactions() {
        return List.copyOf(transactions);
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void addTransaction(Transaction transaction) {
        this.transactions.addFirst(transaction);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Account account)) return false;
        return Objects.equals(accountNumber, account.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(accountNumber);
    }

    @Override
    public String toString() {
        return "%s for %s with balance %s".formatted(accountNumber, ownerName, getFormattedAmount(balance));
    }
}
