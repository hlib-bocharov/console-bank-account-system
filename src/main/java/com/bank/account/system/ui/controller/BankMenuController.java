package com.bank.account.system.ui.controller;

import com.bank.account.system.service.BankService;

public class BankMenuController implements MenuController {

    private BankService bankService;

    public BankMenuController(BankService bankService) {
        this.bankService = bankService;
    }

    @Override
    public void run() {

    }

    private void printOptions() {
        System.out.println("1) Bank summary  2) List all accounts  3) Total holdings  0) Back");
    }

}
