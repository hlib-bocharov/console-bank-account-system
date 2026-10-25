package com.bank.account.system.ui.controller;

import com.bank.account.system.exception.AccountNotFoundException;
import com.bank.account.system.model.Account;
import com.bank.account.system.model.AccountType;
import com.bank.account.system.service.AccountService;
import com.bank.account.system.service.BankService;
import com.bank.account.system.ui.ConsoleIO;

import java.math.BigDecimal;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.function.Supplier;

import static com.bank.account.system.model.AccountType.CHECKING;
import static com.bank.account.system.model.AccountType.SAVINGS;

public class AccountMenuController implements MenuController {

    private AccountService accountService;
    private BankService bankService;
    private Map<Integer, Supplier<Void>> instructionsMap = Map.of(
            1, this::createAccount,
            2, this::showBalance,
            3, this::renameOwner,
            4, this::closeAccount
    );

    public AccountMenuController(AccountService accountService, BankService bankService) {
        this.accountService = accountService;
        this.bankService = bankService;
    }

    @Override
    public void run() {
        System.out.println("\n--- Accounts ---");
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

    private Void createAccount() {
        System.out.print("Owner name: ");
        String owner = ConsoleIO.getStringValue();
        AccountType type = null;
        while (!(type == CHECKING || type == SAVINGS)) {
            System.out.print("Type (CHECKING/SAVINGS): ");
            try {
                type = ConsoleIO.getAccountTypeValue();
            } catch (IllegalArgumentException e) {
            }
        }
        while (true) {
            System.out.print("Initial deposit: ");
            try {
                BigDecimal initialDeposit = ConsoleIO.getBigDecimalValue();
                Account account = accountService.openAccount(owner, type, initialDeposit);
                bankService.registerAccount(account);
                System.out.println("Created " + account);
            } catch (NumberFormatException e) {
                continue;
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
            break;
        }
        return null;
    }

    private Void showBalance() {
        System.out.print("Account number: ");
        String accountNumber = ConsoleIO.getStringValue();
        try {
            BigDecimal balance = accountService.getBalance(accountNumber);
            System.out.printf("Balance of account number %s is : %s%n", accountNumber, balance);
        } catch (IllegalArgumentException | AccountNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    private Void renameOwner() {
        System.out.print("Account number: ");
        String accountNumber = ConsoleIO.getStringValue();
        System.out.print("New owner name: ");
        String owner = ConsoleIO.getStringValue();
        try {
            accountService.renameOwner(accountNumber, owner);
            System.out.println("Account with number " + accountNumber + " renamed to " + owner);
        } catch (AccountNotFoundException | IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    private Void closeAccount() {
        System.out.print("Account number: ");
        String accountNumber = ConsoleIO.getStringValue();
        try {
            accountService.closeAccount(accountNumber);
        } catch (AccountNotFoundException | IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    private void printOptions() {
        System.out.println("1) Create account  2) Show balance  3) Rename owner  4) Close account  0) Back");
    }
}
