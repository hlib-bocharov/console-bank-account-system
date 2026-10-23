package com.bank.account.system.ui;

import java.util.Scanner;

public class ConsoleIO {

    private static Scanner scanner = new Scanner(System.in);

    public static int getNumber() {
        int number = scanner.nextInt();
        scanner.nextLine();
        return number;
    }

    public static String getLine() {
        return scanner.nextLine();
    }
}
