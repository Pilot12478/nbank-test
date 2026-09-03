package iteration2;

import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static Utils.HelperForIteration2.*;
import static Utils.TestDataGenerator.generateUserName;
import static Utils.TestDataGenerator.getDefaultPassword;
import static io.restassured.RestAssured.given;


public class DepositTest {
    private static int accountId;
    private static String userAuthToken;
    private static final int MAX_DEPOSIT_SUM = 5000;
    private static final double MIN_DEPOSIT_SUM = 0.01;
    private static final double STANDARD_SUM = 4999.99;
    private static final double SUM_ABOVE_MAX_LIMIT = 5000.01;
    private static final int NEGATIVE_SUM = -400;
    private static final int ZERO_SUM = 0;
    private static final int INVALID_ACCOUNT = 666;

    public static Stream<Arguments> testDataForSuccessTest() {

        return Stream.of(
                Arguments.of(STANDARD_SUM, 4999.99f),
                Arguments.of(MIN_DEPOSIT_SUM, 0.01f),
                Arguments.of(MAX_DEPOSIT_SUM, 5000f)
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
        String username = generateUserName();
        String password = getDefaultPassword();
        String role = "USER";
        userAuthToken = createUser(username, password, role);
        accountId = createAccount(userAuthToken);

    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("testDataForSuccessTest")
    @DisplayName("Проверка успешного пополнения аккаунта пользователем")
    public void verifyTopUpSuccess(double value, float expectedBalance) {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", userAuthToken)
                .body(String.format("""
                        {
                          "id": %d,
                          "balance": %s
                        }
                        """, accountId, value))
                .post(BASE_URL + "/api/v1/accounts/deposit")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("balance", Matchers.equalTo(expectedBalance));

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", userAuthToken)
                .get(BASE_URL + "/api/v1/customer/profile")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("accounts[0].balance", Matchers.equalTo(expectedBalance));


    }


    @ParameterizedTest(name = "{0}")
    @MethodSource("testDataForNegativeTest")
    @DisplayName("Проверка отсутствия возможности пополнения счета с различными невилидными данными")
    public void shouldNotAllowDeposit(double value, String expectedErrorText) {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", userAuthToken)
                .body(String.format("""
                        {
                          "id": %d,
                          "balance":%s
                        }
                        """, accountId, value))
                .post(BASE_URL + "/api/v1/accounts/deposit")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString(expectedErrorText));
    }

    @Test
    @DisplayName("Проверка отсутствия возможности пополнить аккаунт пользователя, которого не существует")
    public void shouldNotAllowDepositAccountThatNotExist() {
        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("Authorization", userAuthToken)
                .body(String.format("""
                         {
                          "id": %d,
                          "balance":%s
                          }
                        """, INVALID_ACCOUNT, MIN_DEPOSIT_SUM))
                .post(BASE_URL + "/api/v1/accounts/deposit")
                .then()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body(Matchers.containsString("Unauthorized access to account"));
    }


}


