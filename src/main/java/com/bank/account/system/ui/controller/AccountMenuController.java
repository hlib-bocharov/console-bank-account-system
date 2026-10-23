package com.bank.account.system.ui.controller;

import com.bank.account.system.service.AccountService;

public class AccountMenuController implements MenuController {

    private AccountService accountService;

    public AccountMenuController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public void run() {

    }

    private void printOptions() {
        System.out.println("1) Create account  2) Show balance  3) Rename owner  4) Close account  0) Back");
    }
}
