package iteration2.api;


import api.models.DepositModelResponse;
import common.annotations.UserSession;
import common.extensions.TestUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static api.errors.DepositErrors.*;
import static api.steps.AccountSteps.getAccountBalance;
import static api.steps.DepositSteps.*;
import static org.assertj.core.api.Assertions.offset;
import static utils.constants.BankLimits.MAX_DEPOSIT_SUM;
import static utils.constants.BankLimits.MIN_DEPOSIT_SUM;


public class DepositTest extends BaseTest {
    private static final double STANDARD_SUM = 4999.99;
    private static final double SUM_ABOVE_MAX_LIMIT = 5000.01;
    private static final int NEGATIVE_SUM = -400;
    private static final int ZERO_SUM = 0;
    private static final int INVALID_ACCOUNT = 666;
    private static final double INITIAL_BALANCE = 0.0;

    public static Stream<Arguments> testDataForSuccessTest() {

        return Stream.of(
                Arguments.of(STANDARD_SUM, STANDARD_SUM),
                Arguments.of(MIN_DEPOSIT_SUM, MIN_DEPOSIT_SUM),
                Arguments.of(MAX_DEPOSIT_SUM, MAX_DEPOSIT_SUM)
        );
    }

    public static Stream<Arguments> testDataForNegativeTest() {
        return Stream.of(
                Arguments.of(SUM_ABOVE_MAX_LIMIT, EXCEEDS_LIMIT),
                Arguments.of(NEGATIVE_SUM, MIN_AMOUNT),
                Arguments.of(ZERO_SUM, MIN_AMOUNT)

        );
    }

    @ParameterizedTest(name = "{0}")
    @UserSession
    @MethodSource("testDataForSuccessTest")
    @DisplayName("Проверка успешного пополнения аккаунта пользователем")
    public void verifyTopUpSuccess(double sum, double expectedBalance, TestUser user) {
        DepositModelResponse depositModelResponse = depositAccount(user.user(), user.accounts().getFirst(), sum);
        softly.assertThat(depositModelResponse.getBalance()).isCloseTo(expectedBalance, offset(0.001));
        softly.assertThat(getAccountBalance(user.user(), user.accounts().getFirst())).isCloseTo(expectedBalance, offset(0.001));


    }


    @ParameterizedTest(name = "{0}")
    @UserSession
    @MethodSource("testDataForNegativeTest")
    @DisplayName("Проверка отсутствия возможности пополнения счета с различными невалидными данными")
    public void shouldNotAllowDeposit(double sum, String expectedErrorText,TestUser user) {
        String actualErrorMessage = depositExpectingBadRequest(user.user(), user.accounts().getFirst(), sum);
        softly.assertThat(actualErrorMessage).isEqualTo(expectedErrorText);
        softly.assertThat(getAccountBalance(user.user(), user.accounts().getFirst())).isCloseTo(INITIAL_BALANCE, offset(0.001));


    }

    @Test
    @UserSession
    @DisplayName("Проверка отсутствия возможности пополнить аккаунт пользователя, которого не существует")
    public void shouldNotAllowDepositAccountThatNotExist(TestUser user) {
        String actualErrorMessage = depositExpectingForbidden(user.user(), INVALID_ACCOUNT, MIN_DEPOSIT_SUM);
        softly.assertThat(actualErrorMessage).isEqualTo(UNAUTHORIZED_ACCESS);
        softly.assertThat(getAccountBalance(user.user(), user.accounts().getFirst())).isCloseTo(INITIAL_BALANCE, offset(0.001));
    }
}


