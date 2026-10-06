package iteration2.api;

import api.models.CreateTransferModelResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import api.steps.AdminSteps;
import api.steps.DepositSteps;
import api.steps.UserInfo;

import java.util.stream.Stream;

import static api.errors.TransferErrors.*;
import static iteration2.api.TransferAsserts.assertThatTransfer;
import static org.assertj.core.api.Assertions.offset;
import static api.steps.AccountSteps.createAccount;
import static api.steps.AccountSteps.getAccountBalance;
import static api.steps.AdminSteps.createUser;
import static api.steps.TransferSteps.createTransfer;
import static api.steps.TransferSteps.transferExpectingBadRequest;
import static utils.constants.BankLimits.*;

public class TransferTest extends BaseTest {
    private UserInfo userInfo;
    private UserInfo anotherUserInfo;
    private int userAccount;
    private static final double STANDARD_TRANSFER_SUM = 9999.99;
    private static final int ZERO_TRANSFER_SUM = 0;
    private static final double SUM_ABOVE_TRANSFER_LIMIT = 10000.01;
    private static final int NEGATIVE_TRANSFER_SUM = -333;
    private static final int ACCOUNT_THAT_NOT_EXIST = 34434;
    private static final double INITIAL_BALANCE = MAX_DEPOSIT_SUM * 2;



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
        AdminSteps.deleteUser(anotherUserInfo);
    }


    public static Stream<Arguments> testDataForSuccessTest() {
        return Stream.of(
                Arguments.of(MAX_TRANSFER_SUM, INITIAL_BALANCE - MAX_TRANSFER_SUM, MAX_TRANSFER_SUM),
                Arguments.of(MIN_AMOUNT_LIMIT, INITIAL_BALANCE - MIN_AMOUNT_LIMIT, MIN_AMOUNT_LIMIT),
                Arguments.of(STANDARD_TRANSFER_SUM, INITIAL_BALANCE - STANDARD_TRANSFER_SUM, STANDARD_TRANSFER_SUM)

        );
    }

    public static Stream<Arguments> testDataForNegativeTestWithInvalidSum() {
        return Stream.of(
                Arguments.of(ZERO_TRANSFER_SUM, MIN_AMOUNT),
                Arguments.of(NEGATIVE_TRANSFER_SUM, MIN_AMOUNT),
                Arguments.of(SUM_ABOVE_TRANSFER_LIMIT, EXCEEDS_LIMIT)
        );
    }


    @ParameterizedTest(name = "{0}")
    @MethodSource("testDataForSuccessTest")
    @DisplayName("Проверка успешного перевода денежных средств между своими счетами")
    public void checkMeToMeSuccessTransfer(double sum, double expectedSenderBalance, double expectedReceiverBalance) {
        int senderAccountId = userAccount;
        int receiverAccountId = createAccount(userInfo);


        CreateTransferModelResponse response = createTransfer(userInfo, senderAccountId, sum, receiverAccountId);
        assertThatTransfer(response, softly).isSuccessful(sum, senderAccountId, receiverAccountId);

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
        softly.assertThat(actualErrorText).isEqualTo(INVALID_TRANSFER);
        softly.assertThat(getAccountBalance(userInfo, userAccount)).isCloseTo(INITIAL_BALANCE - MAX_TRANSFER_SUM, offset(0.001));

    }

    @Test
    @DisplayName("Проверка успешного перевода денежных средств на сторонний аккаунт")
    public void checkTransferToAlienAccount() {
        anotherUserInfo = createUser();
        int anotherUserAccount = createAccount(anotherUserInfo);

        CreateTransferModelResponse response = createTransfer(userInfo, userAccount, MIN_AMOUNT_LIMIT, anotherUserAccount);

        assertThatTransfer(response, softly).isSuccessful(MIN_AMOUNT_LIMIT, userAccount, anotherUserAccount);

        softly.assertThat(getAccountBalance(userInfo, userAccount)).isCloseTo(INITIAL_BALANCE - MIN_AMOUNT_LIMIT, offset(0.001));
        softly.assertThat(getAccountBalance(anotherUserInfo, anotherUserAccount)).isCloseTo(MIN_AMOUNT_LIMIT, offset(0.001));

    }

    @Test
    @DisplayName("Проверка отсутствия возможности перевода на несуществующий аккаунт")
    public void shouldNotAllowTransferToNotExistAccount() {
        String actualErrorText = transferExpectingBadRequest(userInfo, userAccount, MIN_AMOUNT_LIMIT, ACCOUNT_THAT_NOT_EXIST);

        softly.assertThat(actualErrorText).isEqualTo(INVALID_TRANSFER);
        softly.assertThat(getAccountBalance(userInfo, userAccount)).isCloseTo(INITIAL_BALANCE, offset(0.001));
    }

    @Test
    @DisplayName("Проверка отсутствия возможности перевода со счета на счет если счет один и тот же")
    public void shouldNotAllowTransferIfSenderAndReceiverAccountSame() {
        String actualErrorText = transferExpectingBadRequest(userInfo, userAccount, MIN_AMOUNT_LIMIT, userAccount);
        softly.assertThat(actualErrorText).isEqualTo(INVALID_TRANSFER);
        softly.assertThat(getAccountBalance(userInfo, userAccount)).isCloseTo(INITIAL_BALANCE, offset(0.001));

    }

}
