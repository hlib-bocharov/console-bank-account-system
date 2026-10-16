package com.bank.account.system.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Account {

    private String accountNumber;
    private String ownerName;
    private AccountType type;
    private BigDecimal balance;
    private List<Transaction> transactions;

    public Account() {

    }

    public Account(String accountNumber, String ownerName, AccountType type, BigDecimal balance) {
        this(accountNumber, ownerName, type, balance, new LinkedList<>());
    }

    public Account(String accountNumber, String ownerName, AccountType type, BigDecimal balance, List<Transaction> transactions) {
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.type = type;
        this.balance = balance;
        this.transactions = transactions;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
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

    public void setType(AccountType type) {
        this.type = type;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
}
