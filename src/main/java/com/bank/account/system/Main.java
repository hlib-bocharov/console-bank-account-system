package com.bank.account.system;

import com.bank.account.system.model.Bank;
import com.bank.account.system.ui.controller.MainMenuController;

public class Main {

    public static void main(String[] args) {
        Bank bank = new Bank("Demo Bank", "DB01");
        new MainMenuController(bank).run();
    }
}
