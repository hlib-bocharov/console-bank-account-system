package com.bank.account.system.ui.controller;

import com.bank.account.system.model.Bank;
import com.bank.account.system.service.AccountService;
import com.bank.account.system.service.BankService;
import com.bank.account.system.service.TransactionService;

import java.util.InputMismatchException;
import java.util.Map;

import static com.bank.account.system.ui.ConsoleIO.getLine;
import static com.bank.account.system.ui.ConsoleIO.getNumber;

public class MainMenuController implements MenuController {

    private Bank bank;
    private Map<Integer, MenuController> instructionsMap;

    public MainMenuController(Bank bank) {
        this.bank = bank;
        BankService bankService = new BankService(bank);
        instructionsMap = Map.of(
                1, new BankMenuController(bankService),
                2, new AccountMenuController(new AccountService(bankService), bankService),
                3, new TransactionMenuController(new TransactionService(bankService))
        );
    }

    @Override
    public void run() {
        System.out.println("=== " + bank + " ===");
        printOptions();
        int option;
        while (true) {
            try {
                System.out.print("> ");
                option = getNumber();
                if (option == 0) break;
                instructionsMap.get(option).run();
                System.out.println("\n=== " + bank + " ===");
                printOptions();
            } catch (InputMismatchException e) {
                System.out.println("Option must be integer!");
                getLine();
            } catch (NullPointerException e) {
                System.out.println("Please enter a number from 0 to 3.");
            }
        }
    }

    private void printOptions() {
        System.out.println("1) Bank  2) Accounts  3) Transactions  0) Exit");
    }
}
