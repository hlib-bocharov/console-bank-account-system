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

import static com.bank.account.system.util.AccountPropertyValidation.getBalanceIfValidForAccountType;
import static com.bank.account.system.util.MoneyUtils.getAmountIfValid;
import static com.bank.account.system.util.MoneyUtils.getRoundedAmount;

public class TransactionService {

    private BankService bankService;

    public TransactionService(BankService bankService) {
        this.bankService = bankService;
    }

    public void deposit(String accountNumber, BigDecimal amount) {
        Account account = bankService.findAccount(accountNumber);
        String timestamp = ZonedDateTime.now().toString();
        BigDecimal resultingBalance = account.getBalance().add(getRoundedAmount(getAmountIfValid(amount)));
        account.setBalance(resultingBalance);
        Transaction transaction = new Transaction(TransactionType.DEPOSIT, amount,  timestamp, resultingBalance);
        account.addTransaction(transaction);
    }

    public void withdraw(String accountNumber, BigDecimal amount) throws InsufficientFundsException {
        Account account = bankService.findAccount(accountNumber);
        String timestamp = ZonedDateTime.now().toString();
        BigDecimal resultingBalance = account.getBalance().subtract(getRoundedAmount(getAmountIfValid(amount)));
        try {
            account.setBalance(getBalanceIfValidForAccountType(account.getType(), resultingBalance));
        } catch (IllegalArgumentException e) {
            throw new InsufficientFundsException("Resulting balance cannot be less than the allowed minimum balance for %s account with minimum balance %s"
                    .formatted(account.getType(), account.getType().getAllowedMinimumBalance()), e);
        }
        Transaction transaction = new Transaction(TransactionType.WITHDRAW, amount,  timestamp, resultingBalance);
        account.addTransaction(transaction);
    }

    public void transfer(String fromNumber, String toNumber, BigDecimal amount) throws InsufficientFundsException {
        Account fromAccount = bankService.findAccount(fromNumber);
        withdraw(fromNumber, amount);
        try {
            Account toAccount = bankService.findAccount(fromNumber);
            if (fromAccount.equals(toAccount)) {
                throw new IllegalArgumentException("fromAccount and toAccount cannot be the same account");
            }
            deposit(toNumber, amount);
            String timestamp = toAccount.getTransactions().getFirst().getTimestamp();
            Transaction fromTransaction = fromAccount.getTransactions().getFirst();
            Transaction toTransaction = toAccount.getTransactions().getFirst();
            fromAccount.setTransactions(removeFirstTransactions(fromAccount.getTransactions(), 1));
            toAccount.setTransactions(removeFirstTransactions(toAccount.getTransactions(), 1));
            fromAccount.addTransaction(new Transaction(TransactionType.TRANSFER, amount, timestamp, fromTransaction.getResultingBalance()));
            toAccount.addTransaction(new Transaction(TransactionType.TRANSFER, amount, timestamp, toTransaction.getResultingBalance()));
        } catch (AccountNotFoundException e) {
            deposit(fromNumber, amount);
            fromAccount.setTransactions(removeFirstTransactions(fromAccount.getTransactions(), 2));
            throw e;
        }
    }

    private List<Transaction> removeFirstTransactions(List<Transaction> transactions, long amount) {
        return transactions.stream()
                .skip(amount)
                .collect(LinkedList::new, List::add, List::addAll);
    }

    public List<Transaction> getHistory(String accountNumber) {
        Account account = bankService.findAccount(accountNumber);
        return Collections.unmodifiableList(account.getTransactions());
    }

}
