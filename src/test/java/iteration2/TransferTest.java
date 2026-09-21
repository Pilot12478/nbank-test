package iteration2;

import models.CreateTransferModelResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import steps.AdminSteps;
import steps.DepositSteps;
import steps.UserInfo;

import java.util.stream.Stream;

import static iteration2.TransferAsserts.assertThatTransfer;
import static org.assertj.core.api.Assertions.offset;
import static steps.AccountSteps.createAccount;
import static steps.AccountSteps.getAccountBalance;
import static steps.AdminSteps.createUser;
import static steps.TransferSteps.createTransfer;
import static steps.TransferSteps.transferExpectingBadRequest;

public class TransferTest extends BaseTest {
    private UserInfo userInfo;
    private int userAccount;
    private static final int MAX_DEPOSIT_SUM = 5000;
    private static final int MAX_TRANSFER_SUM = 10000;
    private static final double MIN_TRANSFER_SUM = 0.01;
    private static final double STANDARD_TRANSFER_SUM = 9999.99;
    private static final int ZERO_TRANSFER_SUM = 0;
    private static final double SUM_ABOVE_TRANSFER_LIMIT = 10000.01;
    private static final int NEGATIVE_TRANSFER_SUM = -333;
    private static final int ACCOUNT_THAT_NOT_EXIST = 34434;
    private static final double INITIAL_BALANCE = MAX_DEPOSIT_SUM * 2;

    private static final String MSG_MIN_AMOUNT = "Transfer amount must be at least 0.01";
    private static final String MSG_EXCEEDS_LIMIT = "Transfer amount cannot exceed 10000";
    private static final String MSG_INVALID_TRANSFER = "Invalid transfer: insufficient funds or invalid accounts";
    public static final String MSG_TRANSFER_SUCCESS = "Transfer successful";


    @BeforeEach
    public void setUp() {
        userInfo = createUser();
        userAccount = createAccount(userInfo);
        DepositSteps.depositAccount(userInfo, userAccount, MAX_DEPOSIT_SUM);
        DepositSteps.depositAccount(userInfo, userAccount, MAX_DEPOSIT_SUM);
    }

    @AfterEach
    public void deleteUserAccount() {
        AdminSteps.deleteUser(userInfo);
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
        int senderAccountId = userAccount;
        int receiverAccountId = createAccount(userInfo);


        CreateTransferModelResponse response = createTransfer(userInfo, senderAccountId, sum, receiverAccountId);
        assertThatTransfer(response,softly).isSuccessful(sum, senderAccountId, receiverAccountId);

        softly.assertThat(getAccountBalance(userInfo, senderAccountId)).isCloseTo(expectedSenderBalance, offset(0.001));
        softly.assertThat(getAccountBalance(userInfo, receiverAccountId)).isCloseTo(expectedReceiverBalance, offset(0.001));

    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("testDataForNegativeTestWithInvalidSum")
    @DisplayName("Проверка ошибки при переводе при различных невалидных тестовых данных")
    public void shouldNotAllowTransferTest(double sum, String expectedErrorText) {
        int receiverAccountId = createAccount(userInfo);
        String actualErrorText = transferExpectingBadRequest(userInfo, userAccount, sum, receiverAccountId);

        softly.assertThat(actualErrorText).isEqualTo(expectedErrorText);
        softly.assertThat(getAccountBalance(userInfo, userAccount)).isCloseTo(INITIAL_BALANCE, offset(0.001));

    }

    @Test
    @DisplayName("Проверка перевода суммы, которая превышает баланс отправителя")
    public void shouldNotAllowTransferWhenBalanceHasNotEnoughMoney() {

        int receiverAccountId = createAccount(userInfo);
        createTransfer(userInfo, userAccount, MAX_TRANSFER_SUM, receiverAccountId);
        String actualErrorText = transferExpectingBadRequest(userInfo, userAccount, MAX_TRANSFER_SUM, receiverAccountId);
        softly.assertThat(actualErrorText).isEqualTo(MSG_INVALID_TRANSFER);
        softly.assertThat(getAccountBalance(userInfo, userAccount)).isCloseTo(INITIAL_BALANCE - MAX_TRANSFER_SUM, offset(0.001));

    }

    @Test
    @DisplayName("Проверка успешного перевода денежных средств на сторонний аккаунт")
    public void checkTransferToAlienAccount() {
        UserInfo anotherUserInfo = createUser();
        int anotherUserAccount = createAccount(anotherUserInfo);

        CreateTransferModelResponse response = createTransfer(userInfo, userAccount, MIN_TRANSFER_SUM, anotherUserAccount);

        assertThatTransfer(response,softly).isSuccessful(MIN_TRANSFER_SUM, userAccount, anotherUserAccount);

        softly.assertThat(getAccountBalance(userInfo, userAccount)).isCloseTo(INITIAL_BALANCE - MIN_TRANSFER_SUM, offset(0.001));
        softly.assertThat(getAccountBalance(anotherUserInfo, anotherUserAccount)).isCloseTo(MIN_TRANSFER_SUM, offset(0.001));
        AdminSteps.deleteUser(anotherUserInfo);

    }

    @Test
    @DisplayName("Проверка отсутствия возможности перевода на несуществующий аккаунт")
    public void shouldNotAllowTransferToNotExistAccount() {
        String actualErrorText = transferExpectingBadRequest(userInfo, userAccount, MIN_TRANSFER_SUM, ACCOUNT_THAT_NOT_EXIST);

        softly.assertThat(actualErrorText).isEqualTo(MSG_INVALID_TRANSFER);
        softly.assertThat(getAccountBalance(userInfo, userAccount)).isCloseTo(INITIAL_BALANCE, offset(0.001));
    }

    @Test
    @DisplayName("Проверка отсутствия возможности перевода со счета на счет если счет один и тот же")
    public void shouldNotAllowTransferIfSenderAndReceiverAccountSame() {
        String actualErrorText = transferExpectingBadRequest(userInfo, userAccount, MIN_TRANSFER_SUM, userAccount);
        softly.assertThat(actualErrorText).isEqualTo(MSG_INVALID_TRANSFER);
        softly.assertThat(getAccountBalance(userInfo, userAccount)).isCloseTo(INITIAL_BALANCE, offset(0.001));

    }

}
