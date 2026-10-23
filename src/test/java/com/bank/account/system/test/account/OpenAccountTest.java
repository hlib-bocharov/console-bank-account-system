package com.bank.account.system.test.account;

import com.bank.account.system.model.Account;
import com.bank.account.system.model.AccountType;
import com.bank.account.system.test.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OpenAccountTest extends BaseTest {

    @ParameterizedTest
    @DisplayName("Open account fails when one of parameter is invalid")
    @MethodSource("invalidParams")
    public void testOpenAccountFailsParametersValidation(String ownerName, AccountType type, BigDecimal balance, String expectedExceptionMessage) {
        var exception = assertThrows(IllegalArgumentException.class, () -> accountService.openAccount(ownerName, type, balance),
                "Open account with empty owner name should throw an exception");
        assertEquals(expectedExceptionMessage, exception.getMessage(), "Exception message is incorrect");
    }

    private static Stream<Arguments> invalidParams() {
        return Stream.of(
                Arguments.of(null, AccountType.CHECKING, BigDecimal.TEN, "Owner name cannot be null."),
                Arguments.of("", AccountType.CHECKING, BigDecimal.TEN, "Owner name cannot be blank."),
                Arguments.of("  ", AccountType.CHECKING, BigDecimal.TEN, "Owner name cannot be blank."),
                Arguments.of("John", AccountType.CHECKING, new BigDecimal("-1"), "Balance cannot be less than the allowed minimum balance for CHECKING account with minimum balance 0.00."),
                Arguments.of("John", AccountType.SAVINGS, new BigDecimal("9"), "Balance cannot be less than the allowed minimum balance for SAVINGS account with minimum balance 10.00.")
        );
    }

    @ParameterizedTest
    @DisplayName("Open account successfully returns account")
    @MethodSource("validParams")
    public void testOpenAccountSuccessfullyReturnsAccount(String ownerName, AccountType type, BigDecimal balance) {
        Account actualAccount = accountService.openAccount(ownerName, type, balance);
        Account expectedAccount = new Account("ACC001", ownerName, type, balance.setScale(2));
        verifyEqualityAccountProperties(expectedAccount, actualAccount);
    }

    private static Stream<Arguments> validParams() {
        return Stream.of(
                Arguments.of("John", AccountType.CHECKING, new BigDecimal("0")),
                Arguments.of("John", AccountType.CHECKING, new BigDecimal("1")),
                Arguments.of("John", AccountType.SAVINGS, new BigDecimal("10")),
                Arguments.of("John", AccountType.SAVINGS, new BigDecimal("11"))
        );
    }
}
