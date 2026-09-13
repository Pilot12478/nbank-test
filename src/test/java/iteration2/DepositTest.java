package iteration2;


import models.AccountInfo;
import models.DepositModelResponse;
import models.UserModelResponseProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import specs.ResponseSpecs;

import java.util.stream.Stream;

import static Utils.HelperForIteration2.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class DepositTest {
    private AccountInfo accountInfo;
    private static final int MAX_DEPOSIT_SUM = 5000;
    private static final double MIN_DEPOSIT_SUM = 0.01;
    private static final double STANDARD_SUM = 4999.99;
    private static final double SUM_ABOVE_MAX_LIMIT = 5000.01;
    private static final int NEGATIVE_SUM = -400;
    private static final int ZERO_SUM = 0;
    private static final int INVALID_ACCOUNT = 666;
    public static final double INITIAL_BALANCE = 0.0;

    public static Stream<Arguments> testDataForSuccessTest() {

        return Stream.of(
                Arguments.of(STANDARD_SUM, 4999.99),
                Arguments.of(MIN_DEPOSIT_SUM, 0.01),
                Arguments.of(MAX_DEPOSIT_SUM, 5000)
        );
    }

    public static Stream<Arguments> testDataForNegativeTest() {
        return Stream.of(
                Arguments.of(SUM_ABOVE_MAX_LIMIT, "Deposit amount cannot exceed 5000"),
                Arguments.of(NEGATIVE_SUM, "Deposit amount must be at least 0.01"),
                Arguments.of(ZERO_SUM, "Deposit amount must be at least 0.01")

        );
    }


    @BeforeEach
    public void preconditionForSuccessTest() {
        accountInfo = createUserAndAccount();

    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("testDataForSuccessTest")
    @DisplayName("Проверка успешного пополнения аккаунта пользователем")
    public void verifyTopUpSuccess(double value, double expectedBalance) {
        DepositModelResponse depositModelResponse = depositAccount(accountInfo, value, ResponseSpecs.ok())
                .extract().as(DepositModelResponse.class);
        assertEquals(expectedBalance, depositModelResponse.getBalance(), 0.001);
        assertEquals(expectedBalance, getAccountBalance(accountInfo));

    }


    @ParameterizedTest(name = "{0}")
    @MethodSource("testDataForNegativeTest")
    @DisplayName("Проверка отсутствия возможности пополнения счета с различными невилидными данными")
    public void shouldNotAllowDeposit(double value, String expectedErrorText) {
        String actualErrorMessage = depositAccount(accountInfo, value, ResponseSpecs.badRequest())
                .extract()
                .asString();
        assertEquals(expectedErrorText, actualErrorMessage);
        assertEquals(INITIAL_BALANCE, getAccountBalance(accountInfo), 0.001);


    }

    @Test
    @DisplayName("Проверка отсутствия возможности пополнить аккаунт пользователя, которого не существует")
    public void shouldNotAllowDepositAccountThatNotExist() {
        String actualErrorMessage = depositAccount(accountInfo.getUsername(), accountInfo.getPassword(), INVALID_ACCOUNT, MIN_DEPOSIT_SUM, ResponseSpecs.forbidden())
                .extract()
                .asString();
        assertEquals("Unauthorized access to account", actualErrorMessage);
        assertEquals(INITIAL_BALANCE, getAccountBalance(accountInfo), 0.001);

    }


}


