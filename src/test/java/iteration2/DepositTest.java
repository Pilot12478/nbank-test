package iteration2;


import Utils.AccountInfo;
import models.BaseModel;
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

import static Utils.HelperForIteration2.*;
import static org.assertj.core.api.Assertions.offset;
import static org.junit.jupiter.api.Assertions.assertEquals;


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

    @AfterEach
    public void deleteUserAccount() {
        deleteUser(accountInfo);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("testDataForSuccessTest")
    @DisplayName("Проверка успешного пополнения аккаунта пользователем")
    public void verifyTopUpSuccess(double value, double expectedBalance) {
        DepositModelResponse depositModelResponse = depositAccount(accountInfo, value, ResponseSpecs.ok())
                .extract().as(DepositModelResponse.class);
        softly.assertThat(expectedBalance).isCloseTo(depositModelResponse.getBalance(),offset(0.001));
        softly.assertThat(expectedBalance).isCloseTo(getAccountBalance(accountInfo),offset(0.001));


    }


    @ParameterizedTest(name = "{0}")
    @MethodSource("testDataForNegativeTest")
    @DisplayName("Проверка отсутствия возможности пополнения счета с различными невилидными данными")
    public void shouldNotAllowDeposit(double value, String expectedErrorText) {
        String actualErrorMessage = depositAccount(accountInfo, value, ResponseSpecs.badRequest())
                .extract()
                .asString();
        softly.assertThat(expectedErrorText).isEqualTo(actualErrorMessage);
        softly.assertThat(INITIAL_BALANCE).isCloseTo(getAccountBalance(accountInfo),offset(0.001));


    }

    @Test
    @DisplayName("Проверка отсутствия возможности пополнить аккаунт пользователя, которого не существует")
    public void shouldNotAllowDepositAccountThatNotExist() {
        String actualErrorMessage = depositAccount(accountInfo.getUsername(), accountInfo.getPassword(), INVALID_ACCOUNT, MIN_DEPOSIT_SUM, ResponseSpecs.forbidden())
                .extract()
                .asString();
        softly.assertThat(UNAUTHORIZED_ACCESS).isEqualTo(actualErrorMessage);
        softly.assertThat(INITIAL_BALANCE).isCloseTo(getAccountBalance(accountInfo),offset(0.001));


    }


}


