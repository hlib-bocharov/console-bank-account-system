package com.bank.account.system.ui.controller;

import com.bank.account.system.model.Bank;
import com.bank.account.system.service.AccountService;
import com.bank.account.system.service.BankService;
import com.bank.account.system.service.TransactionService;

import java.util.InputMismatchException;

import static com.bank.account.system.ui.ConsoleIO.getLine;
import static com.bank.account.system.ui.ConsoleIO.getNumber;

public class MainMenuController implements MenuController {

    private Bank bank;
    private BankService bankService;
    private AccountService accountService;
    private TransactionService transactionService;

    public MainMenuController(Bank bank, BankService bankService, AccountService accountService, TransactionService transactionService) {
        this.bank = bank;
        this.bankService = bankService;
        this.accountService = accountService;
        this.transactionService = transactionService;
    }

    @Override
    public void run() {
        System.out.println(bank);
        printOptions();
        int option;
        while (true) {
            try {
                System.out.print("> ");
                option = getNumber();
            } catch (InputMismatchException e) {
                getLine();
                continue;
            }
            switch (option) {
                case 1 -> new BankMenuController(bankService).run();
                case 2 -> new AccountMenuController(accountService).run();
                case 3 -> new TransactionMenuController(transactionService).run();
                case 0 -> System.exit(0);
            }
        }
    }

    private void printOptions() {
        System.out.println("1) Bank  2) Accounts  3) Transactions  0) Exit");
    }
}
