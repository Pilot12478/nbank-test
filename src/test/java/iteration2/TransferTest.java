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

public class TransferTest {
    private static String userAuthToken;
    private static String alienUserAuthToken;
    private static int senderAccountId;
    private static int receiverAccountId;
    private static int alienAccountId;
    private static final int DEPOSIT_SUM = 5000;
    private static final int MAX_TRANSFER_SUM = 10000;
    private static final double MIN_TRANSFER_SUM = 0.01;
    private static final double STANDART_TRANSFER_SUM = 9999.99;
    private static final int ZERO_TRANSFER_SUM = 0;
    private static final double SUM_ABOVE_TRANSFER_LIMIT = 10000.01;
    private static final int NEGATIVE_TRANSFER_SUM = -333;
    private static final int ACCOUNT_THAT_NOT_EXIST = 34434;


    @BeforeAll
    public static void setUp() {
        logConfig();
    }

    @BeforeEach
    public void preconditionForSuccessTest() {
        String userName = generateUserName();
        String password = getDefaultPassword();
        String role = "USER";
        userAuthToken = createUser(userName, password, role);
        senderAccountId = createAccount(userAuthToken);
        receiverAccountId = createAccount(userAuthToken);
        depositAccount(userAuthToken, senderAccountId, DEPOSIT_SUM);
        depositAccount(userAuthToken, senderAccountId, DEPOSIT_SUM);
    }

    public void preconditionForTransferToAlienAccount() {
        String userName = generateUserName();
        String password = getDefaultPassword();
        String role = "USER";
        alienUserAuthToken = createUser(userName, password, role);
        alienAccountId = createAccount(alienUserAuthToken);
    }

    public static Stream<Arguments> testDataForSuccessTest() {
        return Stream.of(
                Arguments.of(MAX_TRANSFER_SUM, MAX_TRANSFER_SUM),
                Arguments.of(MIN_TRANSFER_SUM, MIN_TRANSFER_SUM),
                Arguments.of(STANDART_TRANSFER_SUM, STANDART_TRANSFER_SUM)

        );
    }

    public static Stream<Arguments> testDataForNegativeTestWithInvalidSum() {
        return Stream.of(
                Arguments.of(ZERO_TRANSFER_SUM, "Transfer amount must be at least 0.01"),
                Arguments.of(NEGATIVE_TRANSFER_SUM, "Transfer amount must be at least 0.01"),
                Arguments.of(SUM_ABOVE_TRANSFER_LIMIT, "Transfer amount cannot exceed 10000")
        );
    }


    @ParameterizedTest(name = "{0}")
    @MethodSource("testDataForSuccessTest")
    @DisplayName("Проверка успешного перевода денежных средств между своими счетами")
    public void checkMeToMeSuccessTransfer(double value, double expectedTransfer) {
        given()
                .header("Authorization", userAuthToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format("""
                        {
                          "senderAccountId": %d,
                          "receiverAccountId": %d,
                          "amount": %s
                        }
                        """, senderAccountId, receiverAccountId, value))
                .post(BASE_URL + "/api/v1/accounts/transfer")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("amount", Matchers.is((float) expectedTransfer))
                .body("receiverAccountId", Matchers.equalTo(receiverAccountId))
                .body("senderAccountId", Matchers.equalTo(senderAccountId))
                .body("message", Matchers.equalTo("Transfer successful"));

        given()
                .header("Authorization", userAuthToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .pathParam("accountId", receiverAccountId)
                .get(BASE_URL + "/api/v1/accounts/{accountId}/transactions")
                .then()
                .body("find { it.type == 'TRANSFER_IN' }.amount", Matchers.is((float) expectedTransfer));

        given()
                .header("Authorization", userAuthToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .pathParam("accountId", senderAccountId)
                .get(BASE_URL + "/api/v1/accounts/{accountId}/transactions")
                .then()
                .body("find { it.type == 'TRANSFER_OUT' }.amount", Matchers.is((float) expectedTransfer));


    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("testDataForNegativeTestWithInvalidSum")
    @DisplayName("Проверка ошибки при переводе при различных невалидных тестовых данных")
    public void shouldNotAllowTransferTest(double value, String errorText) {
        given()
                .header("Authorization", userAuthToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format("""
                        {
                          "senderAccountId": %d,
                          "receiverAccountId": %d,
                          "amount": %s
                        }
                        """, senderAccountId, receiverAccountId, value))
                .post(BASE_URL + "/api/v1/accounts/transfer")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString(errorText));
    }

    @Test
    @DisplayName("Проверка перевода суммы, которая превышает баланс отправителя")
    public void shouldNotAllowTransferWhenBalanceHasNotEnoughMoney() {
        given()
                .header("Authorization", userAuthToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format("""
                                 {
                          "senderAccountId": %d,
                          "receiverAccountId": %d,
                          "amount": %s
                        }
                        """, senderAccountId, receiverAccountId, STANDART_TRANSFER_SUM))
                .post(BASE_URL + "/api/v1/accounts/transfer").
                then()
                .statusCode(HttpStatus.SC_OK);

        given()
                .header("Authorization", userAuthToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format("""
                                 {
                          "senderAccountId": %d,
                          "receiverAccountId": %d,
                          "amount": %s
                        }
                        """, senderAccountId, receiverAccountId, STANDART_TRANSFER_SUM))
                .post(BASE_URL + "/api/v1/accounts/transfer").
                then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString("Invalid transfer: insufficient funds or invalid accounts"));

    }

    @Test
    @DisplayName("Проверка успешного перевода денежных средств на сторонний аккаунт")
    public void checkTransferToAlienAccount() {
        preconditionForTransferToAlienAccount();
        given()
                .header("Authorization", userAuthToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format("""
                        {
                          "senderAccountId": %d,
                          "receiverAccountId": %d,
                          "amount": %s
                        }
                        """, senderAccountId, alienAccountId, MIN_TRANSFER_SUM))
                .post(BASE_URL + "/api/v1/accounts/transfer")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("amount", Matchers.is((float) MIN_TRANSFER_SUM))
                .body("receiverAccountId", Matchers.equalTo(alienAccountId))
                .body("senderAccountId", Matchers.equalTo(senderAccountId))
                .body("message", Matchers.equalTo("Transfer successful"));

        given()
                .header("Authorization", alienUserAuthToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .pathParam("accountId", alienAccountId)
                .get(BASE_URL + "/api/v1/accounts/{accountId}/transactions")
                .then()
                .body("find { it.type == 'TRANSFER_IN' }.amount", Matchers.is((float) MIN_TRANSFER_SUM));

        given()
                .header("Authorization", userAuthToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .pathParam("accountId", senderAccountId)
                .get(BASE_URL + "/api/v1/accounts/{accountId}/transactions")
                .then()
                .body("find { it.type == 'TRANSFER_OUT' }.amount", Matchers.is((float) MIN_TRANSFER_SUM));
    }

    @Test
    @DisplayName("Проверка отсутствия возможности перевода на несуществующий аккаунт")
    public void shouldNotAllowTransferToNotExistAccount() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", userAuthToken)
                .body(String.format("""
                            {
                          "senderAccountId": %d,
                          "receiverAccountId": %d,
                          "amount": %s
                        }
                        """, senderAccountId, ACCOUNT_THAT_NOT_EXIST, MIN_TRANSFER_SUM))
                .post(BASE_URL + "/api/v1/accounts/transfer")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString("Invalid transfer: insufficient funds or invalid accounts"));
    }
    @Test
    @DisplayName("Проверка отсутствия возможности перевода со счета на счет если счет один и тот же")
    public void shouldNotAllowTransferIfSenderAndReceiverAccountSame() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", userAuthToken)
                .body(String.format("""
                            {
                          "senderAccountId": %d,
                          "receiverAccountId": %d,
                          "amount": %s
                        }
                        """, senderAccountId, senderAccountId, MIN_TRANSFER_SUM))
                .post(BASE_URL + "/api/v1/accounts/transfer")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString("Invalid transfer: insufficient funds or invalid accounts"));
    }

}
