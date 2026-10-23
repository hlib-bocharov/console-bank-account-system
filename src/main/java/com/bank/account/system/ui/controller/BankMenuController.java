package com.bank.account.system.ui.controller;

import com.bank.account.system.model.Account;
import com.bank.account.system.service.BankService;
import com.bank.account.system.ui.ConsoleIO;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class BankMenuController implements MenuController {

    private BankService bankService;
    private Map<Integer, Supplier<Void>> instructionsMap = Map.of(
            1, this::printBankSummary,
            2, this::printAllAccounts,
            3, this::printTotalHoldings
    );

    public BankMenuController(BankService bankService) {
        this.bankService = bankService;
    }

    @Override
    public void run() {
        System.out.println("\n--- Bank ---");
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

    private Void printBankSummary() {
        System.out.println(bankService.getSummary());
        return null;
    }

    private Void printAllAccounts() {
        List<Account> accounts = bankService.listAccounts();
        if (accounts.isEmpty()) {
            System.out.println("There are no accounts in the system.");
        } else {
            System.out.println("Bank accounts:");
            accounts.forEach(System.out::println);
        }
        return null;
    }

    private Void printTotalHoldings() {
        System.out.println("Total Holdings: " + bankService.getTotalHoldings());
        return null;
    }

    private void printOptions() {
        System.out.println("1) Bank summary  2) List all accounts  3) Total holdings  0) Back");
    }

}
