# Project 1: Console Bank Account System

A console application that manages bank accounts in memory. The goal is to practice core Java OOP: classes, encapsulation, constructors, `static` vs instance members, and exception handling (checked and unchecked).

**Architecture (UI, services, models):**

```
ui (controllers)  ->  service  ->  model (POJOs)
```

- **Models** are plain POJOs: they store state and contain no business logic (`Bank`, `Account`, `Transaction`).
- **Services** manage the models and hold all business logic: `BankService`, `AccountService`, `TransactionService`.
- **UI controllers** are split by menu level: a main menu that opens separate submenus for the bank, accounts, and transactions. No single controller does everything.

There is no repository layer: the project has no external data storage, so the accounts live in the `Bank` model's in-memory `Map`, managed only by `BankService`.

**Stack:** Java 21, Maven (or Gradle), JUnit 5. No frameworks, no database, no external libraries except for testing.

---

## 1. Learning goals

| Topic | Where it shows up |
|---|---|
| Classes and objects | `Bank`, `Account`, `Transaction` (models); three services; four controllers |
| POJO design | Private fields, getters/setters, constructors, `equals`/`hashCode`/`toString` |
| Separation of concerns | Models store state, services hold rules, controllers handle I/O |
| Dependency injection (manual) | Services and controllers receive their dependencies through constructors |
| Constructors | No-arg and all-args constructors, overloading, `this(...)` chaining |
| `static` vs instance | Account number generator, constants, utility class |
| Checked exceptions | `InsufficientFundsException` |
| Unchecked exceptions | `InvalidAmountException`, `AccountNotFoundException` |
| Interfaces | `MenuController` implemented by every controller |
| Collections (light) | `Map<String, Account>`, `List<Transaction>` |
| Console I/O | A single `ConsoleIO` helper wrapping `Scanner` |

---

## 2. Functional requirements

### 2.1 Bank (`BankService`)
- **FR-1** A bank has a name and a code (for example `Demo Bank`, `DB01`) and holds its accounts, keyed by account number.
- **FR-2** `BankService` can register an account in the bank, find an account by number, remove an account, and list all accounts.
- **FR-3** Looking up a missing account throws `AccountNotFoundException`.
- **FR-4** `BankService` reports the total amount of money held across all accounts and the number of accounts.
- **FR-5** `BankService` can return a short bank summary (name, code, account count, total holdings).

### 2.2 Accounts (`AccountService`)
- **FR-6** Create an account with: owner name, account type, optional initial deposit (default 0). The new account is registered in the bank through `BankService`.
- **FR-7** Every account gets a unique account number generated automatically (for example `ACC-0001`, `ACC-0002`). The generator is a `static` member. The user never types the number when creating an account.
- **FR-8** Owner name must be non-null and non-blank. Initial deposit must not be negative. These checks are done by the service before an `Account` is created.
- **FR-9** Two account types, as an enum: `CHECKING` (no overdraft) and `SAVINGS` (minimum balance of 10.00, stored in a `public static final` constant).
- **FR-10** The owner name of an existing account can be changed (same validation as FR-8).
- **FR-11** Closing an account is allowed only when its balance is zero. Otherwise reject the operation with a clear error (your choice of exception; document it).
- **FR-12** Balance inquiry: show the current balance of an account.

### 2.3 Operations (`TransactionService`)
- **FR-13 Deposit:** adds an amount to the balance. Amount must be greater than 0.
- **FR-14 Withdraw:** subtracts an amount from the balance. Amount must be greater than 0 and must not take the balance below the allowed minimum (0 for checking, 10.00 for savings).
- **FR-15 Transfer:** moves money from account A to account B in one operation.
  - Source and target must be different accounts.
  - If the withdrawal from the source fails, nothing changes in either account.
  - If anything fails after the withdrawal, the source balance must be restored.
- **FR-16 Transaction history:** every successful deposit, withdrawal, and transfer is recorded (type, amount, timestamp, resulting balance). The history of an account can be printed. Failed operations are not recorded.
- **FR-17** After creation, the balance is changed **only** by `TransactionService`. The UI never calls `setBalance`. The initial deposit is the one exception: `AccountService` applies it as the starting balance when it creates the account, and it is not recorded in the history.

### 2.4 Console UI (levels)
- **FR-18 Level 0, main menu** (`MainMenuController`):
  1. Bank
  2. Accounts
  3. Transactions
  0. Exit
- **FR-19 Level 1, submenus**, each with `0) Back` to return to the main menu:
  - **Bank menu** (`BankMenuController`): 1) Bank summary, 2) List all accounts, 3) Total holdings
  - **Account menu** (`AccountMenuController`): 1) Create account, 2) Show balance, 3) Rename owner, 4) Close account
  - **Transaction menu** (`TransactionMenuController`): 1) Deposit, 2) Withdraw, 3) Transfer, 4) Show history
- **FR-20** Invalid input (letters where a number is expected, empty line, out-of-range option, malformed amount) must not crash the program: print a clear message and ask again or show the menu again.
- **FR-21** Every exception from the services is caught inside the controller action that called the service and shown as a readable message. No stack traces for the user.

---

## 3. Exception design (the core of this task)

| Exception | Type | Thrown when |
|---|---|---|
| `InsufficientFundsException` | **Checked** (`extends Exception`) | A withdrawal or transfer would break the minimum-balance rule |
| `InvalidAmountException` | **Unchecked** (`extends RuntimeException`) | Amount is zero, negative, or null |
| `AccountNotFoundException` | **Unchecked** | Account number does not exist in the bank |
| `IllegalArgumentException` (built-in) | Unchecked | Null or blank owner name, negative initial deposit, transfer to the same account |

Requirements:
- **EX-1** `InsufficientFundsException` carries useful data: requested amount, current balance, and shortfall, exposed through getters.
- **EX-2** Each custom exception provides at least a message constructor and a message + cause constructor.
- **EX-3** Service methods that can throw the checked exception declare it with `throws`. Callers must handle or propagate it.
- **EX-4** In your README, explain in 3-5 sentences *why* each exception is checked or unchecked.
- **EX-5** Never catch `Exception` broadly in service code. Only the controllers may have a final catch-all per action, and it should log the unexpected error instead of hiding it.

---

## 4. Class and design requirements

```
src/main/java/com/yourname/bank/
├── Main.java                              // wires models, services, and controllers
├── ui/
│   ├── ConsoleIO.java                     // the only class that uses Scanner / System.out
│   └── controller/
│       ├── MenuController.java            // interface: void run()
│       ├── MainMenuController.java        // level 0
│       ├── BankMenuController.java        // level 1
│       ├── AccountMenuController.java     // level 1
│       └── TransactionMenuController.java // level 1
├── model/                                 // POJOs: state only, no business rules
│   ├── Bank.java
│   ├── Account.java
│   ├── AccountType.java                   // enum
│   ├── Transaction.java
│   └── TransactionType.java               // enum
├── service/
│   ├── BankService.java                   // bank-level: registry, lookup, totals
│   ├── AccountService.java                // account lifecycle
│   └── TransactionService.java            // deposit, withdraw, transfer, history
├── exception/
│   ├── InsufficientFundsException.java
│   ├── InvalidAmountException.java
│   └── AccountNotFoundException.java
└── util/
    ├── AccountNumberGenerator.java        // static counter and generator
    └── MoneyUtils.java                    // static validation and formatting helpers
```

### 4.1 Models (POJOs)
- **D-1** All fields are `private`. Provide a public no-arg constructor and an all-args constructor, plus getters and setters where mutation makes sense.
- **D-2** `Bank` fields: `name`, `code`, `accounts` (`Map<String, Account>`). `Account` fields: `accountNumber`, `ownerName`, `type`, `balance`, `transactions`. The account number has a getter but **no setter** (it never changes).
- **D-3** `Transaction` is immutable: all fields `final`, no setters, set through the constructor only.
- **D-4** Models contain **no** validation rules, no business logic, and no console I/O.
- **D-5** `Account.getTransactions()` returns an **unmodifiable copy** (for example `List.copyOf(...)`), never the internal list. Adding a transaction goes through a dedicated `addTransaction(Transaction)` method.
- **D-6** Override `toString()` on all models for readable output.
- **D-7** Override `equals()` and `hashCode()` on `Account`, based on account number only.

### 4.2 Services
- **D-8** Dependencies are passed through constructors. No static singletons.
  ```java
  Bank bank = new Bank("Demo Bank", "DB01");
  BankService bankService = new BankService(bank);
  AccountService accountService = new AccountService(bankService);
  TransactionService transactionService = new TransactionService(bankService);
  ```
- **D-9** Dependencies go **one way only**: `AccountService -> BankService` and `TransactionService -> BankService`. `BankService` depends on nothing but the `Bank` model. No cycles, and the two other services do not depend on each other.
- **D-10** Responsibilities:
  - `BankService` is the only class that touches `Bank.getAccounts()`. It looks accounts up and throws `AccountNotFoundException`. Public methods return unmodifiable views or copies, never the internal map.
  - `AccountService` is the only place that creates `Account` objects and validates owner names. It generates account numbers and enforces the close-account rule.
  - `TransactionService` is the only place that changes balances after creation, validates amounts, creates `Transaction` objects, and throws `InsufficientFundsException` and `InvalidAmountException`.
- **D-11** Suggested methods (adjust names as you like):

  `BankService`
  - `void registerAccount(Account account)`
  - `Account findAccount(String accountNumber)`
  - `void removeAccount(String accountNumber)`
  - `List<Account> listAccounts()`
  - `int getAccountCount()`
  - `BigDecimal getTotalHoldings()`
  - `String getSummary()`

  `AccountService`
  - `Account openAccount(String owner, AccountType type, BigDecimal initialDeposit)`
  - `BigDecimal getBalance(String accountNumber)`
  - `void renameOwner(String accountNumber, String newName)`
  - `void closeAccount(String accountNumber)`

  `TransactionService`
  - `void deposit(String accountNumber, BigDecimal amount)`
  - `void withdraw(String accountNumber, BigDecimal amount) throws InsufficientFundsException`
  - `void transfer(String fromNumber, String toNumber, BigDecimal amount) throws InsufficientFundsException`
  - `List<Transaction> getHistory(String accountNumber)`
- **D-12** Services never print anything and never read input.

### 4.3 UI controllers
- **D-13** Every controller implements `MenuController` (`void run()`). Each controller handles exactly one menu level and one area.
- **D-14** `MainMenuController` shows level 0 and delegates to the other three controllers. It contains no service calls of its own.
- **D-15** Each submenu controller receives only the service(s) it needs, plus the shared `ConsoleIO`, through its constructor. For example, `TransactionMenuController(ConsoleIO io, TransactionService service)`.
- **D-16** `ConsoleIO` is the only class that uses `Scanner` and `System.out`. It offers helpers such as `readLine`, `readInt(min, max)`, `readAmount`, `readEnum`, and `println`, and it re-prompts on malformed input. Create it once in `Main` and share it. Construct it from an `InputStream` and `PrintStream` so it can be tested.
- **D-17** Controllers contain **no business rules**. They only parse input, call a service, and print the result or the error message. Rules such as "amount must be positive" belong in the services.
- **D-18** Each controller action wraps its service call in its own `try/catch` (see FR-21), so one failed action returns to the menu instead of ending the program.

### 4.4 Constructors
- **D-19** Provide constructor overloading on `Account`, for example `Account()` and `Account(String accountNumber, String ownerName, AccountType type, BigDecimal balance)`. Make one constructor delegate to another with `this(...)` at least once.

### 4.5 `static` vs instance
- **D-20** Account number counter: `private static int nextNumber` (or `AtomicInteger`) inside `AccountNumberGenerator`, with a `public static String next()` method.
- **D-21** Constants such as the savings minimum balance are `public static final`.
- **D-22** `MoneyUtils` is a utility class with only `static` methods (amount validation, rounding, formatting), a private constructor, and no instances.
- **D-23** In your README, list which members are `static`, which are instance, and why.

### 4.6 Money handling
- **D-24** Use `BigDecimal` for money, **not** `double`. Scale of 2, rounding mode `HALF_UP`.
- **D-25** Format output as `1,234.50` (use `NumberFormat` or `String.format`).

---

## 5. Non-functional requirements

- **NF-1** Code compiles and runs with `mvn clean test` and `mvn exec:java` (or the Gradle equivalent).
- **NF-2** Java naming conventions, no unused imports, methods under ~25 lines.
- **NF-3** Javadoc on public classes and on the public methods of the three services.
- **NF-4** Use Git: at least 10 meaningful commits (for example one per service, per controller, and per exception).

---

## 6. Testing requirements (JUnit 5)

Write tests **before or alongside** each feature. Services are tested with real objects (a real `Bank` and real services), so Mockito is not needed yet.

**BankService**
- `registerAccount` then `findAccount` returns the same account.
- `findAccount` for an unknown number throws `AccountNotFoundException`.
- `removeAccount` removes the account. A second lookup throws.
- `listAccounts` returns all accounts and the result cannot be used to change the bank's internal state.
- `getTotalHoldings` equals the sum of all balances. `getAccountCount` is correct.
- `getSummary` contains the bank name and code.

**AccountService**
- Opening an account with valid data returns an account with the expected owner, type, and balance, and registers it in the bank.
- Null or blank owner, or negative initial deposit, throws `IllegalArgumentException` and registers nothing.
- Two accounts get different, sequential account numbers.
- `renameOwner` updates the name. A blank name is rejected.
- `closeAccount` removes a zero-balance account and rejects one with money in it.
- `getBalance` for an unknown account throws `AccountNotFoundException`.

**TransactionService**
- `deposit(100)` increases the balance by exactly 100 and records a transaction.
- `deposit(0)` and `deposit(-5)` throw `InvalidAmountException` (use `@ParameterizedTest` for `0`, `-1`, `-0.01`, `null`).
- `withdraw` within the balance succeeds. Exceeding it throws `InsufficientFundsException` and leaves the balance unchanged.
- A savings account cannot go below the minimum balance.
- Successful transfer: source decreases, target increases, total money unchanged, both histories updated.
- Failed transfer (insufficient funds): both balances and histories unchanged.
- Transfer to the same account is rejected.
- Operations on an unknown account number throw `AccountNotFoundException`.
- `getHistory` returns transactions in order and cannot be modified.

**Models**
- `Account.equals`/`hashCode` depend only on the account number.
- `getTransactions()` result cannot be modified (`UnsupportedOperationException`).
- `Transaction` has no way to change its fields after construction.

**Exceptions**
- `InsufficientFundsException` reports the correct requested amount, balance, and shortfall.
- Use `assertThrows` and `assertAll`.

**Utilities and UI helper**
- `AccountNumberGenerator` produces the expected format and increments.
- `MoneyUtils` rounds and formats as specified.
- `ConsoleIO` (fed from a `ByteArrayInputStream`) re-prompts on invalid numbers and out-of-range options, and accepts valid input.

**Target:** at least 90% line coverage on the `service` package (check with JaCoCo).

---

## 7. Acceptance criteria (definition of done)

- [ ] All functional requirements FR-1 to FR-21 work from the console.
- [ ] Layers are respected: controllers -> services -> models. Controllers never change model state directly.
- [ ] Models are POJOs with no business rules. Only `BankService` touches the bank's accounts `Map`.
- [ ] Service dependencies go one way only (D-9), with no cycles.
- [ ] The UI is split into one main controller and three submenu controllers. No controller handles more than one area.
- [ ] Only `ConsoleIO` uses `Scanner` and `System.out`. `model` and `service` contain no console I/O.
- [ ] Exceptions behave as described in section 3, with checked vs unchecked used correctly.
- [ ] No `double` used for money anywhere.
- [ ] All tests pass and the coverage target is met.
- [ ] README includes: how to run, the exception rationale (EX-4), and the `static` vs instance table (D-23).

---

## 8. Design note: trade-offs of the POJO approach

With POJO models, nothing in the language stops other code from calling `account.setBalance(...)` or `bank.getAccounts().clear()` and bypassing the rules. Here the protection is a team convention: only the services change model state. Write 3-5 sentences in your README comparing this with a rich domain model, where `Account.withdraw` enforces its own rules. Both styles are common. The POJO + service + controller style is the one you will meet in Spring Boot projects, where the services become `@Service` beans and the controllers become `@RestController` classes.

---

## 9. Stretch goals (optional)

- Add interest calculation for savings accounts (`static` interest rate, monthly application method in `TransactionService`).
- Add a daily withdrawal limit with its own exception.
- Record the initial deposit as the first history entry without creating a dependency cycle between services.
- Persist the bank and its history to a file on exit and reload on start (preview of projects 8 and 9). This is the point where a repository class becomes worth adding.
- Add a level 2 menu, for example a per-account menu (select an account once, then deposit, withdraw, and view history without retyping the number).
- Make the account number generator thread-safe with `AtomicInteger`.
- Rewrite `Account` as a rich domain model (rules inside the class) and compare the two designs.

---

## 10. Sample session

```
=== Demo Bank (DB01) ===
1) Bank  2) Accounts  3) Transactions  0) Exit
> 2

--- Accounts ---
1) Create account  2) Show balance  3) Rename owner  4) Close account  0) Back
> 1
Owner name: Anna
Type (CHECKING/SAVINGS): CHECKING
Initial deposit: 200
Created ACC-0001 for Anna with balance 200.00
> 0

=== Demo Bank (DB01) ===
1) Bank  2) Accounts  3) Transactions  0) Exit
> 3

--- Transactions ---
1) Deposit  2) Withdraw  3) Transfer  4) Show history  0) Back
> 2
Account number: ACC-0001
Amount: 500
Error: Insufficient funds. Requested 500.00, balance 200.00, short by 300.00.
> 1
Account number: ACC-0001
Amount: -20
Error: Amount must be greater than zero.
> 0
```