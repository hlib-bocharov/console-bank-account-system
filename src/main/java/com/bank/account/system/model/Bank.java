package com.bank.account.system.model;

import java.util.HashMap;
import java.util.Map;

public class Bank {

    private String name;
    private final String code;
    private final Map<String, Account> accounts;

    public Bank() {
        this("Demo Bank", "DB01");
    }

    public Bank(String name, String code) {
        this(name, code, new HashMap<>());
    }

    public Bank(String name, String code, Map<String, Account> accounts) {
        this.name = name;
        this.code = code;
        this.accounts = accounts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public Map<String, Account> getAccounts() {
        return accounts;
    }

}
