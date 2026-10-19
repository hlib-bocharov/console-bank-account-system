package com.bank.account.system.util;

import java.util.concurrent.atomic.AtomicInteger;

public class AccountNumberGenerator {

    private static AtomicInteger counter = new AtomicInteger();
    private static AtomicInteger defaultCounter = new AtomicInteger();

    private AccountNumberGenerator() {}

    public static String next() {
        return "ACC%03d".formatted(counter.incrementAndGet());
    }

    public static String nextDefault() {
        return "DEF%03d".formatted(defaultCounter.incrementAndGet());
    }
}
