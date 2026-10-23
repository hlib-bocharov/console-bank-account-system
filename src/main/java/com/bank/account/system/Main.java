package com.bank.account.system;

import com.bank.account.system.model.Bank;
import com.bank.account.system.service.AccountService;
import com.bank.account.system.service.BankService;
import com.bank.account.system.service.TransactionService;
import com.bank.account.system.ui.controller.MainMenuController;

public class Main {

    public static void main(String[] args) {
        Bank bank = new Bank("Demo Bank", "DB01");
        BankService bankService = new BankService(bank);
        AccountService accountService = new AccountService(bankService);
        TransactionService transactionService = new TransactionService(bankService);
        new MainMenuController(bank, bankService, accountService, transactionService).run();
    }
}
