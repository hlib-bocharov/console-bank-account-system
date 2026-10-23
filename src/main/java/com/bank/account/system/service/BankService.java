package com.bank.account.system.service;

import com.bank.account.system.exception.AccountNotFoundException;
import com.bank.account.system.model.Account;
import com.bank.account.system.model.Bank;

import java.math.BigDecimal;
import java.util.List;

import static com.bank.account.system.util.MoneyUtils.getFormattedAmount;

public class BankService {

    private final Bank bank;

    public BankService(Bank bank) {
        this.bank = bank;
    }

    public void registerAccount(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("Account must be not null.");
        }
        if (account.getAccountNumber() == null) {
            throw new IllegalArgumentException("Account number must be not null.");
        }
        bank.getAccounts().putIfAbsent(account.getAccountNumber(), account);
    }

    public Account findAccount(String accountNumber) {
        if (accountNumber == null) {
            throw new IllegalArgumentException("Account number must be not null.");
        }
        if (accountNumber.isBlank()) {
            throw new IllegalArgumentException("Account number must be not blank.");
        }
        Account account = bank.getAccounts().get(accountNumber.trim());
        if (account == null) {
            throw new AccountNotFoundException("Account number " + accountNumber + " not found.");
        }
        return account;
    }

    public void removeAccount(String accountNumber) {
        Account account = findAccount(accountNumber);
        bank.getAccounts().remove(account.getAccountNumber());
    }

    public List<Account> listAccounts() {
        return List.copyOf(bank.getAccounts().values());
    }

    public int getAccountCount() {
        return bank.getAccounts().size();
    }

    public BigDecimal getTotalHoldings() {
        return bank.getAccounts().values().stream()
                .map(Account::getBalance)
                .reduce(new BigDecimal("0.00"), BigDecimal::add);
    }

    public String getSummary() {
        return "Bank Summary:%n\tname: %s%n\tcode: %s%n\taccount count: %d%n\ttotal holdings: %s%n"
                .formatted(
                        bank.getName(), bank.getCode(), getAccountCount(), getFormattedAmount(getTotalHoldings())
                );
    }

}
