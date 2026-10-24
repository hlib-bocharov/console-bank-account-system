package com.bank.account.system.ui;

import com.bank.account.system.model.AccountType;

import java.math.BigDecimal;
import java.util.Scanner;

public class ConsoleIO {

    private static Scanner scanner = new Scanner(System.in);

    public static int getNumber() {
        int number = scanner.nextInt();
        getLine();
        return number;
    }

    public static String getLine() {
        return scanner.nextLine();
    }

    public static String getStringValue() {
        String value = getLine();
        return value.equalsIgnoreCase("null") ? null : value;
    }

    public static AccountType getAccountTypeValue() {
        return AccountType.valueOf(getLine().toUpperCase());
    }

    public static BigDecimal getBigDecimalValue() {
        String value = getStringValue();
        return value == null ? null : new BigDecimal(value);
    }
}
