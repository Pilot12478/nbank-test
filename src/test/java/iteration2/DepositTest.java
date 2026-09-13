package iteration2;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import models.DepositModelRequest;
import models.DepositModelResponse;
import models.UserModelResponseProfile;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import requests.DepositRequester;
import requests.GetUserProfileRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.stream.Stream;

import static Utils.HelperForIteration2.*;
import static Utils.TestDataGenerator.generateUserName;
import static Utils.TestDataGenerator.getDefaultPassword;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class DepositTest {
    private static int accountId;
    private static String userAuthToken;
    private static String username;
    private static String password;
    private static final int MAX_DEPOSIT_SUM = 5000;
    private static final double MIN_DEPOSIT_SUM = 0.01;
    private static final double STANDARD_SUM = 4999.99;
    private static final double SUM_ABOVE_MAX_LIMIT = 5000.01;
    private static final int NEGATIVE_SUM = -400;
    private static final int ZERO_SUM = 0;
    private static final int INVALID_ACCOUNT = 666;
    private static final double INITIAL_BALANCE = 0.0;

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

    @BeforeAll
    public static void setUp() {
        logConfig();

    }

    @BeforeEach
    public void preconditionForSuccessTest() {
        username = generateUserName();
        password = getDefaultPassword();
        String role = "USER";
        userAuthToken = createUser(username, password, role);
        accountId = createAccount(username, password);

    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("testDataForSuccessTest")
    @DisplayName("Проверка успешного пополнения аккаунта пользователем")
    public void verifyTopUpSuccess(double value, double expectedBalance) {
        DepositModelResponse depositModelResponse = depositAccount(username, password, accountId, value, ResponseSpecs.ok())
                .extract().as(DepositModelResponse.class);
        assertEquals(depositModelResponse.getBalance(), expectedBalance);

        UserModelResponseProfile userModelResponseProfile = getUserAccount(username, password)
                .extract().as(UserModelResponseProfile.class);
        assertEquals(expectedBalance,userModelResponseProfile.getAccounts().getFirst().getBalance());
    }


    @ParameterizedTest(name = "{0}")
    @MethodSource("testDataForNegativeTest")
    @DisplayName("Проверка отсутствия возможности пополнения счета с различными невилидными данными")
    public void shouldNotAllowDeposit(double value, String expectedErrorText) {
        String actualErrorMessage = depositAccount(username, password, accountId, value, ResponseSpecs.badRequest())
                .extract()
                .asString();
        assertEquals(actualErrorMessage, expectedErrorText);

        UserModelResponseProfile userModelResponseProfile = getUserAccount(username, password)
                .extract().as(UserModelResponseProfile.class);
        assertEquals(userModelResponseProfile.getAccounts().get(0).getBalance(), INITIAL_BALANCE);


    }

    @Test
    @DisplayName("Проверка отсутствия возможности пополнить аккаунт пользователя, которого не существует")
    public void shouldNotAllowDepositAccountThatNotExist() {
        String actualErrorMessage = new DepositRequester(RequestSpecs.userAuthReq(username, password)
                , ResponseSpecs.forbidden())
                .send(DepositModelRequest
                        .builder()
                        .id(INVALID_ACCOUNT)
                        .balance(MIN_DEPOSIT_SUM)
                        .build())
                .extract()
                .asString();
        assertEquals(actualErrorMessage, "Unauthorized access to account");

        UserModelResponseProfile userModelResponseProfile = new GetUserProfileRequester(
                RequestSpecs.userAuthReq(username, password), ResponseSpecs.ok())
                .send()
                .extract()
                .as(UserModelResponseProfile.class);
        assertEquals(INITIAL_BALANCE, userModelResponseProfile.getAccounts().get(0).getBalance());

    }


}


