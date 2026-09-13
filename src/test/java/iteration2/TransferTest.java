package iteration2;

import models.AccountInfo;
import models.CreateTransferModelResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import specs.ResponseSpecs;

import java.util.stream.Stream;

import static Utils.HelperForIteration2.*;
import static Utils.HelperForIteration2.getAccountBalance;
import static iteration2.DepositTest.INITIAL_BALANCE;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransferTest {
    private AccountInfo senderAccountInfo;
    private AccountInfo receiverAccountInfo;
    private static final int DEPOSIT_SUM = 5000;
    private static final int MAX_TRANSFER_SUM = 10000;
    private static final double MIN_TRANSFER_SUM = 0.01;
    private static final double STANDART_TRANSFER_SUM = 9999.99;
    private static final int ZERO_TRANSFER_SUM = 0;
    private static final double SUM_ABOVE_TRANSFER_LIMIT = 10000.01;
    private static final int NEGATIVE_TRANSFER_SUM = -333;
    private static final int ACCOUNT_THAT_NOT_EXIST = 34434;

    private static final String MSG_MIN_AMOUNT = "Transfer amount must be at least 0.01";
    private static final String MSG_EXCEEDS_LIMIT = "Transfer amount cannot exceed 10000";
    private static final String MSG_INVALID_TRANSFER = "Invalid transfer: insufficient funds or invalid accounts";
    private static final String MSG_TRANSFER_SUCCESS = "Transfer successful";


    @BeforeEach
    public void preconditionForSuccessTest() {
        senderAccountInfo = createUserAndAccount();
        depositAccount(senderAccountInfo, DEPOSIT_SUM, ResponseSpecs.ok());
        depositAccount(senderAccountInfo, DEPOSIT_SUM, ResponseSpecs.ok());
    }

    public void preconditionForTransferToAlienAccount() {
        receiverAccountInfo = createUserAndAccount();

    }

    public static Stream<Arguments> testDataForSuccessTest() {
        return Stream.of(
                Arguments.of(MAX_TRANSFER_SUM, INITIAL_BALANCE, MAX_TRANSFER_SUM),
                Arguments.of(MIN_TRANSFER_SUM, STANDART_TRANSFER_SUM, MIN_TRANSFER_SUM),
                Arguments.of(STANDART_TRANSFER_SUM, MIN_TRANSFER_SUM, STANDART_TRANSFER_SUM)

        );
    }

    public static Stream<Arguments> testDataForNegativeTestWithInvalidSum() {
        return Stream.of(
                Arguments.of(ZERO_TRANSFER_SUM, MSG_MIN_AMOUNT),
                Arguments.of(NEGATIVE_TRANSFER_SUM, MSG_MIN_AMOUNT),
                Arguments.of(SUM_ABOVE_TRANSFER_LIMIT, MSG_EXCEEDS_LIMIT)
        );
    }


    @ParameterizedTest(name = "{0}")
    @MethodSource("testDataForSuccessTest")
    @DisplayName("Проверка успешного перевода денежных средств между своими счетами")
    public void checkMeToMeSuccessTransfer(double value, double expectedSenderBalance, double expectedReceiverBalance) {
        int senderAccountId = senderAccountInfo.getAccountId();
        int receiverAccountId = createAccount(senderAccountInfo.getUsername(), senderAccountInfo.getPassword());


        CreateTransferModelResponse response = createTransfer(senderAccountInfo, value, receiverAccountId, ResponseSpecs.ok())
                .extract().as(CreateTransferModelResponse.class);
        assertEquals(value, response.getAmount());
        assertEquals(receiverAccountId, response.getReceiverAccountId(), 0.001);
        assertEquals(senderAccountId, response.getSenderAccountId());
        assertEquals(MSG_TRANSFER_SUCCESS, response.getMessage());

        assertEquals(expectedSenderBalance, getAccountBalance(senderAccountInfo, senderAccountId), 0.001);
        assertEquals(expectedReceiverBalance, getAccountBalance(senderAccountInfo, receiverAccountId), 0.001);


    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("testDataForNegativeTestWithInvalidSum")
    @DisplayName("Проверка ошибки при переводе при различных невалидных тестовых данных")
    public void shouldNotAllowTransferTest(double value, String expectedErrorText) {
        int receiverAccountId = createAccount(senderAccountInfo.getUsername(), senderAccountInfo.getPassword());
        String actualErrorText = createTransfer(senderAccountInfo, value, receiverAccountId, ResponseSpecs.badRequest())
                .extract().asString();

        assertEquals(expectedErrorText, actualErrorText);

    }

    @Test
    @DisplayName("Проверка перевода суммы, которая превышает баланс отправителя")
    public void shouldNotAllowTransferWhenBalanceHasNotEnoughMoney() {

        int receiverAccountId = createAccount(senderAccountInfo.getUsername(), senderAccountInfo.getPassword());
        createTransfer(senderAccountInfo, MAX_TRANSFER_SUM, receiverAccountId, ResponseSpecs.ok());
        String actualErrorText = createTransfer(senderAccountInfo, MAX_TRANSFER_SUM, receiverAccountId, ResponseSpecs.badRequest())
                .extract().asString();
        assertEquals(MSG_INVALID_TRANSFER, actualErrorText);

    }

    @Test
    @DisplayName("Проверка успешного перевода денежных средств на сторонний аккаунт")
    public void checkTransferToAlienAccount() {
        preconditionForTransferToAlienAccount();
        int senderAccountId = senderAccountInfo.getAccountId();
        int receiverAccountId = receiverAccountInfo.getAccountId();
        CreateTransferModelResponse response = createTransfer(senderAccountInfo, MIN_TRANSFER_SUM, receiverAccountId, ResponseSpecs.ok())
                .extract().as(CreateTransferModelResponse.class);
        assertEquals(MIN_TRANSFER_SUM, response.getAmount());
        assertEquals(receiverAccountId, response.getReceiverAccountId());
        assertEquals(senderAccountId, response.getSenderAccountId());
        assertEquals(MSG_TRANSFER_SUCCESS, response.getMessage());


        assertEquals(STANDART_TRANSFER_SUM, getAccountBalance(senderAccountInfo), 0.001);
        assertEquals(MIN_TRANSFER_SUM, getAccountBalance(receiverAccountInfo), 0.001);

    }

    @Test
    @DisplayName("Проверка отсутствия возможности перевода на несуществующий аккаунт")
    public void shouldNotAllowTransferToNotExistAccount() {
        String actualErrorText = createTransfer(senderAccountInfo, MIN_TRANSFER_SUM, ACCOUNT_THAT_NOT_EXIST, ResponseSpecs.badRequest())
                .extract().asString();

        assertEquals(MSG_INVALID_TRANSFER, actualErrorText);
    }

    @Test
    @DisplayName("Проверка отсутствия возможности перевода со счета на счет если счет один и тот же")
    public void shouldNotAllowTransferIfSenderAndReceiverAccountSame() {


        String actualErrorText = createTransfer(senderAccountInfo, MIN_TRANSFER_SUM, senderAccountInfo.getAccountId(), ResponseSpecs.badRequest())
                .extract().asString();

        assertEquals(MSG_INVALID_TRANSFER, actualErrorText);

    }

}
