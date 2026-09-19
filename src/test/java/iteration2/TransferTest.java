package iteration2;

import utils.AccountInfo;
import models.CreateTransferModelResponse;
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

public class TransferTest extends BaseTest {
    private AccountInfo senderAccountInfo;
    private static final int DEPOSIT_SUM = 5000;
    private static final int MAX_TRANSFER_SUM = 10000;
    private static final double MIN_TRANSFER_SUM = 0.01;
    private static final double STANDARD_TRANSFER_SUM = 9999.99;
    private static final int ZERO_TRANSFER_SUM = 0;
    private static final double SUM_ABOVE_TRANSFER_LIMIT = 10000.01;
    private static final int NEGATIVE_TRANSFER_SUM = -333;
    private static final int ACCOUNT_THAT_NOT_EXIST = 34434;
    private static final double INITIAL_BALANCE = DEPOSIT_SUM * 2;

    private static final String MSG_MIN_AMOUNT = "Transfer amount must be at least 0.01";
    private static final String MSG_EXCEEDS_LIMIT = "Transfer amount cannot exceed 10000";
    private static final String MSG_INVALID_TRANSFER = "Invalid transfer: insufficient funds or invalid accounts";
    private static final String MSG_TRANSFER_SUCCESS = "Transfer successful";


    @BeforeEach
    public void setUp() {
        senderAccountInfo = createUserAndAccount();
        depositAccountPositive(senderAccountInfo, DEPOSIT_SUM, ResponseSpecs.ok());
        depositAccountPositive(senderAccountInfo, DEPOSIT_SUM, ResponseSpecs.ok());
    }

    @AfterEach
    public void deleteUserAccount() {
        deleteUser(senderAccountInfo);
    }


    public static Stream<Arguments> testDataForSuccessTest() {
        return Stream.of(
                Arguments.of(MAX_TRANSFER_SUM, INITIAL_BALANCE - MAX_TRANSFER_SUM, MAX_TRANSFER_SUM),
                Arguments.of(MIN_TRANSFER_SUM, INITIAL_BALANCE - MIN_TRANSFER_SUM, MIN_TRANSFER_SUM),
                Arguments.of(STANDARD_TRANSFER_SUM, INITIAL_BALANCE - STANDARD_TRANSFER_SUM, STANDARD_TRANSFER_SUM)

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
    public void checkMeToMeSuccessTransfer(double sum, double expectedSenderBalance, double expectedReceiverBalance) {
        int senderAccountId = senderAccountInfo.getAccountId();
        int receiverAccountId = createAccount(senderAccountInfo.getUsername(), senderAccountInfo.getPassword()).getId();


        CreateTransferModelResponse response = createTransferPositive(senderAccountInfo, sum, receiverAccountId, ResponseSpecs.ok());

        softly.assertThat(response.getAmount()).isCloseTo(sum, offset(0.001));
        softly.assertThat(response.getReceiverAccountId()).isEqualTo(receiverAccountId);
        softly.assertThat(response.getSenderAccountId()).isEqualTo(senderAccountId);
        softly.assertThat(response.getMessage()).isEqualTo(MSG_TRANSFER_SUCCESS);

        softly.assertThat(getAccountBalance(senderAccountInfo, senderAccountId)).isCloseTo(expectedSenderBalance, offset(0.001));
        softly.assertThat(getAccountBalance(senderAccountInfo, receiverAccountId)).isCloseTo(expectedReceiverBalance, offset(0.001));

    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("testDataForNegativeTestWithInvalidSum")
    @DisplayName("Проверка ошибки при переводе при различных невалидных тестовых данных")
    public void shouldNotAllowTransferTest(double sum, String expectedErrorText) {
        int receiverAccountId = createAccount(senderAccountInfo.getUsername(), senderAccountInfo.getPassword()).getId();
        String actualErrorText = createTransferNegative(senderAccountInfo, sum, receiverAccountId, ResponseSpecs.badRequest())
                .extract().asString();

        softly.assertThat(actualErrorText).isEqualTo(expectedErrorText);
        softly.assertThat(getAccountBalance(senderAccountInfo)).isCloseTo(INITIAL_BALANCE, offset(0.001));

    }

    @Test
    @DisplayName("Проверка перевода суммы, которая превышает баланс отправителя")
    public void shouldNotAllowTransferWhenBalanceHasNotEnoughMoney() {

        int receiverAccountId = createAccount(senderAccountInfo.getUsername(), senderAccountInfo.getPassword()).getId();
        createTransferPositive(senderAccountInfo, MAX_TRANSFER_SUM, receiverAccountId, ResponseSpecs.ok());
        String actualErrorText = createTransferNegative(senderAccountInfo, MAX_TRANSFER_SUM, receiverAccountId, ResponseSpecs.badRequest())
                .extract().asString();
        softly.assertThat(actualErrorText).isEqualTo(MSG_INVALID_TRANSFER);
        softly.assertThat(getAccountBalance(senderAccountInfo)).isCloseTo(INITIAL_BALANCE, offset(0.001));

    }

    @Test
    @DisplayName("Проверка успешного перевода денежных средств на сторонний аккаунт")
    public void checkTransferToAlienAccount() {
        AccountInfo receiverAccountInfo = createUserAndAccount();
        int senderAccountId = senderAccountInfo.getAccountId();
        int receiverAccountId = receiverAccountInfo.getAccountId();
        CreateTransferModelResponse response = createTransferPositive(senderAccountInfo, MIN_TRANSFER_SUM, receiverAccountId, ResponseSpecs.ok());

        softly.assertThat(response.getAmount()).isCloseTo(MIN_TRANSFER_SUM, offset(0.001));
        softly.assertThat(response.getReceiverAccountId()).isEqualTo(receiverAccountId);
        softly.assertThat(response.getSenderAccountId()).isEqualTo(senderAccountId);
        softly.assertThat(response.getMessage()).isEqualTo(MSG_TRANSFER_SUCCESS);

        softly.assertThat(getAccountBalance(senderAccountInfo)).isCloseTo(INITIAL_BALANCE - MIN_TRANSFER_SUM, offset(0.001));
        softly.assertThat(getAccountBalance(receiverAccountInfo)).isCloseTo(MIN_TRANSFER_SUM, offset(0.001));
        deleteUser(receiverAccountInfo);

    }

    @Test
    @DisplayName("Проверка отсутствия возможности перевода на несуществующий аккаунт")
    public void shouldNotAllowTransferToNotExistAccount() {
        String actualErrorText = createTransferNegative(senderAccountInfo, MIN_TRANSFER_SUM, ACCOUNT_THAT_NOT_EXIST, ResponseSpecs.badRequest())
                .extract().asString();

        softly.assertThat(actualErrorText).isEqualTo(MSG_INVALID_TRANSFER);
        softly.assertThat(getAccountBalance(senderAccountInfo)).isCloseTo(INITIAL_BALANCE, offset(0.001));
    }

    @Test
    @DisplayName("Проверка отсутствия возможности перевода со счета на счет если счет один и тот же")
    public void shouldNotAllowTransferIfSenderAndReceiverAccountSame() {
        String actualErrorText = createTransferNegative(senderAccountInfo, MIN_TRANSFER_SUM, senderAccountInfo.getAccountId(), ResponseSpecs.badRequest())
                .extract().asString();


        softly.assertThat(actualErrorText).isEqualTo(MSG_INVALID_TRANSFER);
        softly.assertThat(getAccountBalance(senderAccountInfo)).isCloseTo(INITIAL_BALANCE, offset(0.001));

    }

}
