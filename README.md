# Project 1: Console Bank Account System

A console application that manages bank accounts in memory. The goal is to practice core OOP in Java: classes, encapsulation, constructors, `static` vs instance members, and exception handling (checked and unchecked).

**Stack:** Java 21, Maven (or Gradle), JUnit 5. No frameworks, no database, no external libraries except for testing.

---

## 1. Learning goals

| Topic | Where it shows up |
|---|---|
| Classes and objects | `Account`, `Bank`, `Transaction` |
| Encapsulation | Private fields, no setters for balance, defensive copies |
| Constructors | Validation in constructors, constructor overloading, `this(...)` chaining |
| `static` vs instance | Account number generator, bank-wide counters, utility class |
| Checked exceptions | `InsufficientFundsException` |
| Unchecked exceptions | `InvalidAmountException`, `AccountNotFoundException` |
| Collections (light) | `Map<String, Account>`, `List<Transaction>` |
| Console I/O | `Scanner` menu loop |

---

## 2. Functional requirements

### 2.1 Accounts
- **FR-1** Create an account with: owner name, optional initial deposit (default 0).
- **FR-2** Every account gets a unique account number generated automatically (for example `ACC-0001`, `ACC-0002`). The generator must be a `static` member; the user never types the number when creating an account.
- **FR-3** Owner name must be non-null and non-blank. Initial deposit must not be negative.
- **FR-4** Support two account types through a simple field or enum (`CHECKING`, `SAVINGS`). Overdraft rules differ: checking accounts allow no overdraft in this version; savings accounts have a minimum balance of 10.00 (configurable `static final` constant). *Optional stretch: model these as subclasses once you reach inheritance in project 2.*
- **FR-5** Balance can only be changed through `deposit`, `withdraw`, and `transfer`. There is **no** `setBalance`.

### 2.2 Operations
- **FR-6 Deposit:** adds an amount to the balance. Amount must be greater than 0.
- **FR-7 Withdraw:** subtracts an amount from the balance. Amount must be greater than 0 and must not take the balance below the allowed minimum (0 for checking, 10.00 for savings).
- **FR-8 Transfer:** moves money from account A to account B in one operation.
  - Source and target must be different accounts.
  - If the withdrawal from the source fails, nothing changes in either account.
  - If anything fails after the withdrawal, the source balance must be restored.
- **FR-9 Balance inquiry:** show current balance for an account.
- **FR-10 Transaction history:** every successful deposit, withdrawal, and transfer is recorded (type, amount, timestamp, resulting balance). A user can print the history of an account. Failed operations are not recorded.

### 2.3 Bank (the container)
- **FR-11** A `Bank` class stores accounts and looks them up by account number.
- **FR-12** Looking up a missing account throws `AccountNotFoundException`.
- **FR-13** Bank can list all accounts and report the total amount of money held across all accounts.

### 2.4 Console menu
- **FR-14** A text menu loop with at least these options:
  1. Create account
  2. Deposit
  3. Withdraw
  4. Transfer
  5. Show balance
  6. Show transaction history
  7. List all accounts
  0. Exit
- **FR-15** Invalid menu input (letters, empty line, out-of-range number) must not crash the program: print a clear message and show the menu again.
- **FR-16** Every exception thrown by the domain is caught in the menu layer and translated to a readable message. No stack traces shown to the user.

---

## 3. Exception design (the core of this task)

| Exception | Type | Thrown when |
|---|---|---|
| `InsufficientFundsException` | **Checked** (`extends Exception`) | A withdrawal or transfer would break the minimum-balance rule |
| `InvalidAmountException` | **Unchecked** (`extends RuntimeException`) | Amount is zero, negative, or `NaN`/infinite |
| `AccountNotFoundException` | **Unchecked** | Account number does not exist in the bank |
| `IllegalArgumentException` (built-in) | Unchecked | Null or blank owner name, negative initial deposit, transfer to the same account |

Requirements:
- **EX-1** `InsufficientFundsException` must carry useful data: requested amount, current balance, and shortfall, exposed through getters.
- **EX-2** Each custom exception provides at least a message constructor and a message + cause constructor.
- **EX-3** Methods that can throw the checked exception declare it with `throws`; callers must handle or propagate it.
- **EX-4** Write a short note in your README (3-5 sentences) explaining *why* each exception is checked or unchecked.
- **EX-5** Never catch `Exception` broadly in domain code. Only the menu layer may have a final catch-all, and it should log the unexpected error rather than hide it.

---

## 4. Class and design requirements

Suggested structure (you may adjust, but keep the responsibilities separate):

```
src/main/java/com/yourname/bank/
├── Main.java                  // entry point, creates Bank and ConsoleMenu
├── ui/ConsoleMenu.java        // all Scanner/System.out code lives here only
├── model/
│   ├── Account.java
│   ├── AccountType.java       // enum
│   ├── Transaction.java
│   └── TransactionType.java   // enum
├── service/Bank.java
├── exception/
│   ├── InsufficientFundsException.java
│   ├── InvalidAmountException.java
│   └── AccountNotFoundException.java
└── util/AccountNumberGenerator.java   // or a static member inside Account
```

### Encapsulation
- **D-1** All fields `private`. Fields that never change after construction are `final`.
- **D-2** `Account` exposes read-only getters (`getAccountNumber()`, `getOwnerName()`, `getBalance()`, `getType()`).
- **D-3** `getTransactions()` returns an **unmodifiable copy** (for example `List.copyOf(...)`), never the internal list.
- **D-4** The domain layer (`model`, `service`) contains **no** `System.out` or `Scanner` calls.

### Constructors
- **D-5** `Account` has at least two constructors: `Account(String owner, AccountType type)` and `Account(String owner, AccountType type, BigDecimal initialDeposit)`. The first delegates to the second using `this(...)`.
- **D-6** Validation happens in the constructor. An invalid object must never exist.

### `static` vs instance
- **D-7** Account number counter: `private static int nextNumber` (or `AtomicInteger`), with a `private static` method to produce the next number.
- **D-8** Constants such as the savings minimum balance are `public static final`.
- **D-9** A small utility class (for example `MoneyUtils`) with `static` helper methods such as amount validation and formatting. It has a private constructor and cannot be instantiated.
- **D-10** In your README, list which members are `static`, which are instance, and why.

### Money handling
- **D-11** Use `BigDecimal` for money, **not** `double`. Scale of 2, rounding mode `HALF_UP`.
- **D-12** Format output as `1,234.50` (use `NumberFormat` or `String.format`).

### Object basics
- **D-13** Override `toString()` on `Account` and `Transaction` for readable output.
- **D-14** Override `equals()` and `hashCode()` on `Account` based on account number only.

---

## 5. Non-functional requirements

- **NF-1** Code compiles and runs with `mvn clean test` and `mvn exec:java` (or the Gradle equivalent).
- **NF-2** Java naming conventions, no unused imports, methods under ~25 lines.
- **NF-3** Javadoc on public classes and public methods of `Account` and `Bank`.
- **NF-4** Use Git: at least 8 meaningful commits (for example one per feature or per exception).

---

## 6. Testing requirements (JUnit 5)

Write tests **before or alongside** each feature.

**Account**
- Creating an account with valid data gives the expected owner, number, and balance.
- Creating an account with null/blank owner or negative deposit throws `IllegalArgumentException`.
- Two accounts get different, sequential account numbers.
- `deposit(100)` increases the balance by exactly 100.
- `deposit(0)` and `deposit(-5)` throw `InvalidAmountException`.
- `withdraw` within the balance succeeds; exceeding it throws `InsufficientFundsException` and leaves the balance unchanged.
- Savings account cannot go below the minimum balance.
- The returned transaction list cannot be modified (`UnsupportedOperationException`).

**Bank**
- Find existing account works; unknown number throws `AccountNotFoundException`.
- Successful transfer: source decreases, target increases, total money unchanged.
- Failed transfer (insufficient funds): both balances unchanged.
- Transfer to the same account is rejected.

**Exceptions**
- `InsufficientFundsException` reports correct requested amount, balance, and shortfall.
- Use `assertThrows` and `assertAll`; use `@ParameterizedTest` for invalid amounts (`0`, `-1`, `-0.01`).

**Target:** at least 90% line coverage on `model` and `service` packages (check with JaCoCo).

---

## 7. Acceptance criteria (definition of done)

- [ ] All functional requirements FR-1 to FR-16 work from the console.
- [ ] Exceptions behave as described in section 3, with checked vs unchecked used correctly.
- [ ] No `double` used for money anywhere.
- [ ] Domain code has no console I/O.
- [ ] All tests pass; coverage target met.
- [ ] README includes: how to run, the exception rationale (EX-4), and the `static` vs instance table (D-10).

---

## 8. Stretch goals (optional)

- Add interest calculation for savings accounts (`static` interest rate, monthly application method).
- Persist accounts and history to a file on exit and reload on start (preview of project 8/9).
- Add a daily withdrawal limit with its own exception.
- Replace the type field with `CheckingAccount` / `SavingsAccount` subclasses after finishing project 2.
- Make the account number generator thread-safe with `AtomicInteger`.

---

## 9. Sample session

```
=== Bank Menu ===
1) Create account  2) Deposit  3) Withdraw  4) Transfer
5) Show balance    6) History  7) List accounts  0) Exit
> 1
Owner name: Anna
Type (CHECKING/SAVINGS): CHECKING
Initial deposit: 200
Created ACC-0001 for Anna with balance 200.00

> 3
Account number: ACC-0001
Amount: 500
Error: Insufficient funds. Requested 500.00, balance 200.00, short by 300.00.

> 2
Account number: ACC-0001
Amount: -20
Error: Amount must be greater than zero.
```
