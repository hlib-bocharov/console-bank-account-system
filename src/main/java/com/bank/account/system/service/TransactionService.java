package com.bank.account.system.service;

import com.bank.account.system.exception.AccountNotFoundException;
import com.bank.account.system.exception.InsufficientFundsException;
import com.bank.account.system.model.Account;
import com.bank.account.system.model.Transaction;
import com.bank.account.system.model.TransactionType;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.bank.account.system.util.AccountPropertyValidation.checkBalanceIsValidForAccountType;
import static com.bank.account.system.util.TransactionPropertyValidation.checkAmountIsValid;

public class TransactionService {

    private BankService bankService;

    public TransactionService(BankService bankService) {
        this.bankService = bankService;
    }

    public void deposit(String accountNumber, BigDecimal amount) {
        Account account = bankService.findAccount(accountNumber);
        String timestamp = ZonedDateTime.now().toString();
        BigDecimal resultingBalance = account.getBalance().add(checkAmountIsValid(amount));
        account.setBalance(resultingBalance);
        Transaction transaction = new Transaction(TransactionType.DEPOSIT, amount,  timestamp, resultingBalance);
        account.getTransactions().addFirst(transaction);
    }

    public void withdraw(String accountNumber, BigDecimal amount) throws InsufficientFundsException {
        Account account = bankService.findAccount(accountNumber);
        String timestamp = ZonedDateTime.now().toString();
        BigDecimal resultingBalance = account.getBalance().subtract(checkAmountIsValid(amount));
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
        withdraw(fromNumber, amount);
        Account fromAccount = bankService.findAccount(fromNumber);
        try {
            deposit(toNumber, amount);
            Account toAccount = bankService.findAccount(fromNumber);
            toAccount.getTransactions().getFirst().setType(TransactionType.TRANSFER);
            String timestamp = toAccount.getTransactions().getFirst().getTimestamp();
            fromAccount.getTransactions().getFirst().setTimestamp(timestamp);
            fromAccount.getTransactions().getFirst().setType(TransactionType.TRANSFER);
        } catch (AccountNotFoundException e) {
            deposit(fromNumber, amount);
            List<Transaction> transactions = fromAccount.getTransactions().stream()
                    .skip(2)
                    .collect(LinkedList::new, List::add, List::addAll);
            fromAccount.setTransactions(transactions);
            throw e;
        }
    }

    public List<Transaction> getHistory(String accountNumber) {
        Account account = bankService.findAccount(accountNumber);
        return Collections.unmodifiableList(account.getTransactions());
    }

}
