package com.bank.account.system.service;

import com.bank.account.system.model.Account;
import com.bank.account.system.model.AccountType;

import java.math.BigDecimal;

import static com.bank.account.system.util.AccountNumberGenerator.next;
import static com.bank.account.system.util.AccountPropertyValidation.getBalanceIfValidForAccountType;
import static com.bank.account.system.util.AccountPropertyValidation.checkOwnerNameIsValid;

public class AccountService {

    private final BankService bankService;

    public AccountService(BankService bankService) {
        this.bankService = bankService;
    }

    public Account openAccount(String owner, AccountType type, BigDecimal initialDeposit) {
        return new Account(next(),
                    checkOwnerNameIsValid(owner),
                    type,
                    getBalanceIfValidForAccountType(type, initialDeposit));
    }

    public BigDecimal getBalance(String accountNumber) {
        return bankService.findAccount(accountNumber).getBalance();
    }

    public void renameOwner(String accountNumber, String newName) {
        Account account = bankService.findAccount(accountNumber);
        account.setOwnerName(checkOwnerNameIsValid(newName));
    }

    public void closeAccount(String accountNumber) {
        Account account = bankService.findAccount(accountNumber);
        if (account.getBalance().compareTo(BigDecimal.ZERO) == 0) {
            bankService.removeAccount(accountNumber);
        }
    }
}
