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
        BigDecimal checkedAmount = getRoundedAmount(getAmountIfValid(amount));
        Account account = bankService.findAccount(accountNumber);
        String timestamp = ZonedDateTime.now().toString();
        BigDecimal resultingBalance = account.getBalance().add(checkedAmount);
        account.setBalance(resultingBalance);
        Transaction transaction = new Transaction(TransactionType.DEPOSIT, checkedAmount,  timestamp, resultingBalance);
        account.addTransaction(transaction);
    }

    public void withdraw(String accountNumber, BigDecimal amount) throws InsufficientFundsException {
        BigDecimal checkedAmount = getRoundedAmount(getAmountIfValid(amount));
        Account account = bankService.findAccount(accountNumber);
        String timestamp = ZonedDateTime.now().toString();
        BigDecimal resultingBalance = account.getBalance().subtract(checkedAmount);
        try {
            account.setBalance(getBalanceIfValidForAccountType(account.getType(), resultingBalance));
        } catch (IllegalArgumentException e) {
            throw new InsufficientFundsException("Insufficient funds. Requested %s, balance %s, short by %s."
                    .formatted(checkedAmount, account.getBalance(), checkedAmount.subtract(account.getBalance()).add(account.getType().getAllowedMinimumBalance())), e);
        }
        Transaction transaction = new Transaction(TransactionType.WITHDRAW, amount,  timestamp, resultingBalance);
        account.addTransaction(transaction);
    }

    public void transfer(String fromNumber, String toNumber, BigDecimal amount) throws InsufficientFundsException {
        BigDecimal checkedAmount = getRoundedAmount(getAmountIfValid(amount));
        Account fromAccount = bankService.findAccount(fromNumber);
        withdraw(fromNumber, amount);
        try {
            Account toAccount = bankService.findAccount(toNumber);
            if (fromAccount.equals(toAccount)) {
                throw new IllegalArgumentException("Cannot transfer amount to the same account.");
            }
            deposit(toNumber, checkedAmount);
            String timestamp = toAccount.getTransactions().getFirst().getTimestamp();
            Transaction fromTransaction = fromAccount.getTransactions().getFirst();
            Transaction toTransaction = toAccount.getTransactions().getFirst();
            fromAccount.setTransactions(removeFirstTransactions(fromAccount.getTransactions(), 1));
            toAccount.setTransactions(removeFirstTransactions(toAccount.getTransactions(), 1));
            fromAccount.addTransaction(new Transaction(TransactionType.TRANSFER, amount, timestamp, fromTransaction.getResultingBalance()));
            toAccount.addTransaction(new Transaction(TransactionType.TRANSFER, amount, timestamp, toTransaction.getResultingBalance()));
        } catch (AccountNotFoundException | IllegalArgumentException e) {
            deposit(fromNumber, checkedAmount);
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
