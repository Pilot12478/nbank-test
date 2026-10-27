package iteration3.apiWithDB;

import api.models.dto.DepositModelResponse;

import common.annotations.ApiVersion;
import common.annotations.DataBase;
import common.annotations.UserSession;
import common.assertions.AccountApiAsserts;
import common.assertions.CommonSoftAssertions;
import common.extensions.TestUser;
import db.asserts.AccountDbAsserts;
import iteration2.api.BaseTest;
import org.jooq.DSLContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static api.errors.DepositErrors.*;

import static api.steps.DepositSteps.*;
import static utils.constants.BankLimits.MAX_DEPOSIT_SUM;
import static utils.constants.BankLimits.MIN_DEPOSIT_SUM;

@ApiVersion(versionApi = "with_database_with_fix")
public class DepositTestDB extends BaseTest {
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
    public void verifyTopUpSuccess(double sum, double expectedBalance, TestUser user, @DataBase DSLContext dslContext) {
        DepositModelResponse depositModelResponse = depositAccount(user, sum);
        CommonSoftAssertions.assertThat(softly).isCloseTo(depositModelResponse.getBalance(), expectedBalance);
        AccountApiAsserts.assertThatAccount(softly, user).hasBalance(expectedBalance);
        AccountDbAsserts.assertThatAccount(softly, dslContext, user.accountId()).hasBalance(expectedBalance);
    }


    @ParameterizedTest(name = "{0}")
    @UserSession
    @MethodSource("testDataForNegativeTest")
    @DisplayName("Проверка отсутствия возможности пополнения счета с различными невалидными данными")
    public void shouldNotAllowDeposit(double sum, String expectedErrorText, TestUser user, @DataBase DSLContext dslContext) {
        String actualErrorMessage = depositExpectingBadRequest(user, sum);
        CommonSoftAssertions.assertThat(softly).isEqualTo(actualErrorMessage, expectedErrorText);
        AccountApiAsserts.assertThatAccount(softly, user).hasBalance(INITIAL_BALANCE);
        AccountDbAsserts.assertThatAccount(softly, dslContext, user.accountId()).hasBalance(INITIAL_BALANCE);
    }

    @Test
    @UserSession
    @DisplayName("Проверка отсутствия возможности пополнить аккаунт пользователя, которого не существует")
    public void shouldNotAllowDepositAccountThatNotExist(TestUser user, @DataBase DSLContext dslContext) {
        String actualErrorMessage = depositExpectingForbidden(user.credentials(), INVALID_ACCOUNT, MIN_DEPOSIT_SUM);
        CommonSoftAssertions.assertThat(softly).isEqualTo(actualErrorMessage, UNAUTHORIZED_ACCESS);
        AccountApiAsserts.assertThatAccount(softly, user).hasBalance(INITIAL_BALANCE);
        AccountDbAsserts.assertThatAccount(softly, dslContext, user.accountId()).hasBalance(INITIAL_BALANCE);
    }
}
