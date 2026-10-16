package com.bank.account.system.util;

public class AccountNumberGenerator {

    private static int counter = 0;

    public static String generateAccountNumber() {
        return "ACC%03d".formatted(++counter);
    }
}
