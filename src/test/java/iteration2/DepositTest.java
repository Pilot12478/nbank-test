package iteration2;


import utils.AccountInfo;
import models.DepositModelResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import specs.ResponseSpecs;

import java.util.stream.Stream;

import static utils.HelperForIteration2.*;
import static org.assertj.core.api.Assertions.offset;


public class DepositTest extends BaseTest {
    private AccountInfo accountInfo;
    private static final int MAX_DEPOSIT_SUM = 5000;
    private static final double MIN_DEPOSIT_SUM = 0.01;
    private static final double STANDARD_SUM = 4999.99;
    private static final double SUM_ABOVE_MAX_LIMIT = 5000.01;
    private static final int NEGATIVE_SUM = -400;
    private static final int ZERO_SUM = 0;
    private static final int INVALID_ACCOUNT = 666;
    public static final double INITIAL_BALANCE = 0.0;
    private static final String UNAUTHORIZED_ACCESS = "Unauthorized access to account";

    public static Stream<Arguments> testDataForSuccessTest() {

        return Stream.of(
                Arguments.of(STANDARD_SUM, STANDARD_SUM),
                Arguments.of(MIN_DEPOSIT_SUM, MIN_DEPOSIT_SUM),
                Arguments.of(MAX_DEPOSIT_SUM, MAX_DEPOSIT_SUM)
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
    public void setUp() {
        accountInfo = createUserAndAccount();


    }

    @AfterEach
    public void deleteUserAccount() {
        deleteUser(accountInfo);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("testDataForSuccessTest")
    @DisplayName("Проверка успешного пополнения аккаунта пользователем")
    public void verifyTopUpSuccess(double sum, double expectedBalance) {
        DepositModelResponse depositModelResponse = depositAccountPositive(accountInfo, sum, ResponseSpecs.ok());
        softly.assertThat(depositModelResponse.getBalance()).isCloseTo(expectedBalance, offset(0.001));
        softly.assertThat(getAccountBalance(accountInfo)).isCloseTo(expectedBalance, offset(0.001));


    }


    @ParameterizedTest(name = "{0}")
    @MethodSource("testDataForNegativeTest")
    @DisplayName("Проверка отсутствия возможности пополнения счета с различными невалидными данными")
    public void shouldNotAllowDeposit(double sum, String expectedErrorText) {
        String actualErrorMessage = depositAccountNegative(accountInfo, sum, ResponseSpecs.badRequest()).extract().asString();
        softly.assertThat(actualErrorMessage).isEqualTo(expectedErrorText);
        softly.assertThat(getAccountBalance(accountInfo)).isCloseTo(INITIAL_BALANCE, offset(0.001));


    }

    @Test
    @DisplayName("Проверка отсутствия возможности пополнить аккаунт пользователя, которого не существует")
    public void shouldNotAllowDepositAccountThatNotExist() {
        AccountInfo invalidAcc = createUserAndAccount();
        invalidAcc.setAccountId(INVALID_ACCOUNT);
        String actualErrorMessage = depositAccountNegative(invalidAcc, MIN_DEPOSIT_SUM, ResponseSpecs.forbidden()).extract().asString();
        softly.assertThat(actualErrorMessage).isEqualTo(UNAUTHORIZED_ACCESS);
        softly.assertThat(getAccountBalance(accountInfo)).isCloseTo(INITIAL_BALANCE, offset(0.001));
        deleteUser(invalidAcc);


    }


}


