package com.bank.account.system.ui.controller;

import com.bank.account.system.service.TransactionService;

public class TransactionMenuController implements MenuController {

    private TransactionService transactionService;

    public TransactionMenuController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Override
    public void run() {

    }

    private void printOptions() {
        System.out.println("1) Deposit  2) Withdraw  3) Transfer  4) Show history  0) Back");
    }
}
