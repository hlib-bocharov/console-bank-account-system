package com.bank.account.system.ui.controller;

import com.bank.account.system.exception.AccountNotFoundException;
import com.bank.account.system.exception.InsufficientFundsException;
import com.bank.account.system.exception.InvalidAmountException;
import com.bank.account.system.model.Transaction;
import com.bank.account.system.service.TransactionService;
import com.bank.account.system.ui.ConsoleIO;

import java.math.BigDecimal;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class TransactionMenuController implements MenuController {

    private TransactionService transactionService;
    private Map<Integer, Supplier<Void>> instructionsMap = Map.of(
            1, this::deposit,
            2, this::withdraw,
            3, this::transfer,
            4, this::showHistory
    );

    public TransactionMenuController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Override
    public void run() {
        System.out.println("\n--- Transactions ---");
        printOptions();
        int option;
        while (true) {
            try {
                System.out.print("> ");
                option = ConsoleIO.getNumber();
                if (option == 0) break;
                instructionsMap.get(option).get();
            } catch (InputMismatchException e) {
                System.out.println("Option must be integer!");
                ConsoleIO.getLine();
            } catch (NullPointerException e) {
                System.out.println("Please enter a number from 0 to 3.");
            }
        }
    }

    private Void deposit() {
        System.out.print("Account number: ");
        String accountNumber = ConsoleIO.getStringValue();
        while (true) {
            try {
                System.out.print("Amount: ");
                BigDecimal amount = ConsoleIO.getBigDecimalValue();
                transactionService.deposit(accountNumber, amount);
                System.out.println("Deposited amount: " + amount + " to account: " + accountNumber);
            } catch (NumberFormatException e) {
                continue;
            } catch (AccountNotFoundException | IllegalArgumentException | InvalidAmountException e) {
                System.out.println("Error: " + e.getMessage());
            }
            break;
        }
        return null;
    }

    private Void withdraw() {
        System.out.print("Account number: ");
        String accountNumber = ConsoleIO.getStringValue();
        while (true) {
            try {
                System.out.print("Amount: ");
                BigDecimal amount = ConsoleIO.getBigDecimalValue();
                transactionService.withdraw(accountNumber, amount);
                System.out.println("Withdraw amount: " + amount + " to account: " + accountNumber);
            } catch (NumberFormatException e) {
                continue;
            } catch (AccountNotFoundException | IllegalArgumentException | InvalidAmountException |
                     InsufficientFundsException e) {
                System.out.println("Error: " + e.getMessage());
            }
            break;
        }
        return null;
    }

    private Void transfer() {
        System.out.print("From account number: ");
        String fromAccount = ConsoleIO.getStringValue();
        System.out.print("To account number: ");
        String toAccount = ConsoleIO.getStringValue();
        while (true) {
            try {
                System.out.print("Amount: ");
                BigDecimal amount = ConsoleIO.getBigDecimalValue();
                transactionService.transfer(fromAccount, toAccount, amount);
                System.out.println("Transferred amount: " + amount + " from account: " + fromAccount + " to account: " + toAccount);
            } catch (NumberFormatException e) {
                continue;
            } catch (AccountNotFoundException | IllegalArgumentException | InvalidAmountException |
                     InsufficientFundsException e) {
                System.out.println("Error: " + e.getMessage());
            }
            break;
        }
        return null;
    }

    private Void showHistory() {
        System.out.print("Account number: ");
        String accountNumber = ConsoleIO.getStringValue();
        List<Transaction> transactions = null;
        try {
            transactions = transactionService.getHistory(accountNumber);
        } catch (AccountNotFoundException | IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
        if (transactions.isEmpty()) {
            System.out.println("No history found for account: " + accountNumber);
        } else {
            transactions.forEach(System.out::println);
        }
        return null;
    }

    private void printOptions() {
        System.out.println("1) Deposit  2) Withdraw  3) Transfer  4) Show history  0) Back");
    }
}
