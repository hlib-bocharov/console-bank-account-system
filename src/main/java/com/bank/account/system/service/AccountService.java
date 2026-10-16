package com.bank.account.system.service;

import com.bank.account.system.model.Account;
import com.bank.account.system.model.AccountType;

import java.math.BigDecimal;

import static com.bank.account.system.util.AccountNumberGenerator.generateAccountNumber;
import static com.bank.account.system.util.AccountPropertyValidation.checkInitialDepositIsValid;
import static com.bank.account.system.util.AccountPropertyValidation.checkOwnerNameIsValid;

public class AccountService {

    private final BankService bankService;

    public AccountService(BankService bankService) {
        this.bankService = bankService;
    }

    public Account openAccount(String owner, AccountType type, BigDecimal initialDeposit) {
        return new Account(generateAccountNumber(),
                    checkOwnerNameIsValid(owner),
                    type,
                    checkInitialDepositIsValid(type, initialDeposit));
    }

    public BigDecimal getBalance(String accountNumber) {
        return null;
    }

    public void renameOwner(String accountNumber, String newName) {

    }

    public void closeAccount(String accountNumber) {

    }
}
