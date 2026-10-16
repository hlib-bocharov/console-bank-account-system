package com.bank.account.system.service;

import com.bank.account.system.exception.AccountNotFoundException;
import com.bank.account.system.model.Account;
import com.bank.account.system.model.Bank;

import java.math.BigDecimal;
import java.util.List;

public class BankService {

    private final Bank bank;

    public BankService(Bank bank) {
       this.bank = bank;
    }

    public void registerAccount(Account account) {
        if (account == null || account.getAccountNumber() == null) {
            throw new NullPointerException();
        }
        bank.getAccounts().putIfAbsent(account.getAccountNumber(), account);
    }

    public Account findAccount(String accountNumber) {
        Account account = bank.getAccounts().get(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException();
        }
        return account;
    }

    public void removeAccount(String accountNumber) {
        try {
            findAccount(accountNumber);
            bank.getAccounts().remove(accountNumber);
        } catch (AccountNotFoundException e) {
            throw new AccountNotFoundException();
        }
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
                .reduce(new BigDecimal(0), BigDecimal::add);
    }

    public String getSummary() {
        return "Bank Summary:%nname: %s%ncode: %s%naccount count: %d%ntotal holdings: %s%n"
                .formatted(
                        bank.getName(), bank.getCode(), getAccountCount(), getTotalHoldings()
                );
    }

}
