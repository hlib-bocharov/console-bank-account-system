package com.bank.account.system.service;

import com.bank.account.system.exception.InsufficientFundsException;
import com.bank.account.system.model.Account;
import com.bank.account.system.model.Transaction;
import com.bank.account.system.model.TransactionType;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import static com.bank.account.system.util.AccountPropertyValidation.checkBalanceIsValidForAccountType;

public class TransactionService {

    private BankService bankService;

    public TransactionService(BankService bankService) {
        this.bankService = bankService;
    }

    public void deposit(String accountNumber, BigDecimal amount) {
        Account account = bankService.findAccount(accountNumber);
        String timestamp = ZonedDateTime.now().toString();
        BigDecimal resultingBalance = account.getBalance().add(amount);
        account.setBalance(resultingBalance);
        Transaction transaction = new Transaction(TransactionType.DEPOSIT, amount,  timestamp, resultingBalance);
        account.getTransactions().addFirst(transaction);
    }

    public void withdraw(String accountNumber, BigDecimal amount) throws InsufficientFundsException {
        Account account = bankService.findAccount(accountNumber);
        String timestamp = ZonedDateTime.now().toString();
        BigDecimal resultingBalance = account.getBalance().subtract(amount);
        try {
            account.setBalance(checkBalanceIsValidForAccountType(account.getType(), resultingBalance));
        } catch (IllegalArgumentException e) {
            throw new InsufficientFundsException("Resulting balance cannot be less than the allowed minimum balance for %s account with minimum balance %s"
                    .formatted(account.getType(), account.getType().getAllowedMinimumBalance()));
        }
        Transaction transaction = new Transaction(TransactionType.WITHDRAW, amount,  timestamp, resultingBalance);
        account.getTransactions().addFirst(transaction);
    }

    public void transfer(String fromNumber, String toNumber, BigDecimal amount) throws InsufficientFundsException {

    }

    public List<Transaction> getHistory(String accountNumber) {
        return null;
    }

}
