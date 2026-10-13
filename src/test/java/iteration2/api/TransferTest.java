package iteration2.api;

import api.models.CreateTransferModelResponse;
import common.annotations.ExtraUser;
import common.annotations.UserSession;
import common.extensions.TestUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static api.errors.TransferErrors.*;
import static api.steps.AccountSteps.createAccount;
import static api.steps.AccountSteps.getAccountBalance;
import static api.steps.TransferSteps.createTransfer;
import static api.steps.TransferSteps.transferExpectingBadRequest;
import static iteration2.api.TransferAsserts.assertThatTransfer;
import static org.assertj.core.api.Assertions.offset;
import static utils.constants.BankLimits.*;

public class TransferTest extends BaseTest {
    private static final double STANDARD_TRANSFER_SUM = 9999.99;
    private static final int ZERO_TRANSFER_SUM = 0;
    private static final double SUM_ABOVE_TRANSFER_LIMIT = 10000.01;
    private static final int NEGATIVE_TRANSFER_SUM = -333;
    private static final int ACCOUNT_THAT_NOT_EXIST = 34434;
    private static final double INITIAL_BALANCE = MAX_DEPOSIT_SUM * 2;


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
    @UserSession(accounts = 2,balance = INITIAL_BALANCE)
    @MethodSource("testDataForSuccessTest")
    @DisplayName("Проверка успешного перевода денежных средств между своими счетами")
    public void checkMeToMeSuccessTransfer(double sum, double expectedSenderBalance, double expectedReceiverBalance, TestUser sender) {
        int senderAccountId = sender.accounts().getFirst();
        int receiverAccountId = sender.accounts().get(1);


        CreateTransferModelResponse response = createTransfer(sender.user(), senderAccountId, sum, receiverAccountId);
        assertThatTransfer(response, softly).isSuccessful(sum, senderAccountId, receiverAccountId);

        softly.assertThat(getAccountBalance(sender.user(), senderAccountId)).isCloseTo(expectedSenderBalance, offset(0.001));
        softly.assertThat(getAccountBalance(sender.user(), receiverAccountId)).isCloseTo(expectedReceiverBalance, offset(0.001));

    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("testDataForNegativeTestWithInvalidSum")
    @UserSession(accounts = 2,balance = INITIAL_BALANCE)
    @DisplayName("Проверка ошибки при переводе при различных невалидных тестовых данных")
    public void shouldNotAllowTransferTest(double sum, String expectedErrorText,TestUser sender) {
        int receiverAccountId = createAccount(sender.user());
        String actualErrorText = transferExpectingBadRequest(sender.user(), sender.accounts().getFirst(), sum, receiverAccountId);

        softly.assertThat(actualErrorText).isEqualTo(expectedErrorText);
        softly.assertThat(getAccountBalance(sender.user(), sender.accounts().getFirst())).isCloseTo(INITIAL_BALANCE, offset(0.001));

    }

    @Test
    @DisplayName("Проверка перевода суммы, которая превышает баланс отправителя")
    @UserSession(accounts = 2,balance = INITIAL_BALANCE)
    public void shouldNotAllowTransferWhenBalanceHasNotEnoughMoney(TestUser sender) {
        int senderAccountId = sender.accounts().getFirst();
        int receiverAccountId = sender.accounts().get(1);
        createTransfer(sender.user(), senderAccountId, MAX_TRANSFER_SUM, receiverAccountId);
        String actualErrorText = transferExpectingBadRequest(sender.user(), senderAccountId, MAX_TRANSFER_SUM, receiverAccountId);
        softly.assertThat(actualErrorText).isEqualTo(INVALID_TRANSFER);
        softly.assertThat(getAccountBalance(sender.user(), senderAccountId)).isCloseTo(INITIAL_BALANCE - MAX_TRANSFER_SUM, offset(0.001));

    }

    @Test
    @UserSession(balance = INITIAL_BALANCE)
    @DisplayName("Проверка успешного перевода денежных средств на сторонний аккаунт")
    public void checkTransferToAlienAccount(TestUser sender,@ExtraUser TestUser receiver) {
        int senderAccountId = sender.accounts().getFirst();
        int receiverAccountId = receiver.accounts().getFirst();

        CreateTransferModelResponse response = createTransfer(sender.user(), senderAccountId, MIN_AMOUNT_LIMIT, receiverAccountId);

        assertThatTransfer(response, softly).isSuccessful(MIN_AMOUNT_LIMIT, senderAccountId, receiverAccountId);

        softly.assertThat(getAccountBalance(sender.user(), senderAccountId)).isCloseTo(INITIAL_BALANCE - MIN_AMOUNT_LIMIT, offset(0.001));
        softly.assertThat(getAccountBalance(receiver.user(), receiverAccountId)).isCloseTo(MIN_AMOUNT_LIMIT, offset(0.001));

    }


    @Test
    @UserSession(balance = INITIAL_BALANCE)
    @DisplayName("Проверка отсутствия возможности перевода на несуществующий аккаунт")
    public void shouldNotAllowTransferToNotExistAccount(TestUser sender) {
        String actualErrorText = transferExpectingBadRequest(sender.user(), sender.accounts().getFirst(), MIN_AMOUNT_LIMIT, ACCOUNT_THAT_NOT_EXIST);

        softly.assertThat(actualErrorText).isEqualTo(INVALID_TRANSFER);
        softly.assertThat(getAccountBalance(sender.user(), sender.accounts().getFirst())).isCloseTo(INITIAL_BALANCE, offset(0.001));
    }

    @Test
    @UserSession(balance = INITIAL_BALANCE)
    @DisplayName("Проверка отсутствия возможности перевода со счета на счет если счет один и тот же")
    public void shouldNotAllowTransferIfSenderAndReceiverAccountSame(TestUser sender) {
        String actualErrorText = transferExpectingBadRequest(sender.user(), sender.accounts().getFirst(), MIN_AMOUNT_LIMIT, sender.accounts().getFirst());
        softly.assertThat(actualErrorText).isEqualTo(INVALID_TRANSFER);
        softly.assertThat(getAccountBalance(sender.user(), sender.accounts().getFirst())).isCloseTo(INITIAL_BALANCE, offset(0.001));

    }

}
