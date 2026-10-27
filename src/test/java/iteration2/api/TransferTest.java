package iteration2.api;

import api.models.dto.CreateTransferModelResponse;
import common.annotations.ApiVersion;
import common.annotations.ExtraUser;
import common.annotations.UserSession;
import common.assertions.AccountApiAsserts;
import common.assertions.CommonSoftAssertions;
import common.extensions.TestUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static api.errors.TransferErrors.*;
import static api.steps.AccountSteps.createAccount;
import static api.steps.TransferSteps.createTransfer;
import static api.steps.TransferSteps.transferExpectingBadRequest;
import static common.assertions.TransferAsserts.assertThatTransfer;
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
    @UserSession(accounts = 2, balance = INITIAL_BALANCE)
    @MethodSource("testDataForSuccessTest")
    @DisplayName("Проверка успешного перевода денежных средств между своими счетами")
    @ApiVersion(versionApi = "self-transfer-fix")
    public void checkMeToMeSuccessTransfer(double sum, double expectedSenderBalance, double expectedReceiverBalance, TestUser sender) {
        int senderAccountId = sender.accountId();
        int receiverAccountId = sender.accountId(1);
        CreateTransferModelResponse response = createTransfer(sender, senderAccountId, sum, receiverAccountId);
        assertThatTransfer(softly, response).isSuccessful(sum, senderAccountId, receiverAccountId);
        AccountApiAsserts.assertThatAccount(softly, sender, senderAccountId).hasBalance(expectedSenderBalance);
        AccountApiAsserts.assertThatAccount(softly, sender, receiverAccountId).hasBalance(expectedReceiverBalance);

    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("testDataForNegativeTestWithInvalidSum")
    @UserSession(accounts = 2, balance = INITIAL_BALANCE)
    @DisplayName("Проверка ошибки при переводе при различных невалидных тестовых данных")
    @ApiVersion(versionApi = "self-transfer-fix")
    public void shouldNotAllowTransferTest(double sum, String expectedErrorText, TestUser sender) {
        int receiverAccountId = createAccount(sender);
        String actualErrorText = transferExpectingBadRequest(sender, sender.accountId(), sum, receiverAccountId);
        CommonSoftAssertions.assertThat(softly).isEqualTo(actualErrorText, expectedErrorText);
        AccountApiAsserts.assertThatAccount(softly, sender).hasBalance(INITIAL_BALANCE);

    }

    @Test
    @DisplayName("Проверка перевода суммы, которая превышает баланс отправителя")
    @UserSession(accounts = 2, balance = INITIAL_BALANCE)
    @ApiVersion(versionApi = "self-transfer-fix")
    public void shouldNotAllowTransferWhenBalanceHasNotEnoughMoney(TestUser sender) {
        int senderAccountId = sender.accountId();
        int receiverAccountId = sender.accountId(1);
        createTransfer(sender, senderAccountId, MAX_TRANSFER_SUM, receiverAccountId);
        String actualErrorText = transferExpectingBadRequest(sender, senderAccountId, MAX_TRANSFER_SUM, receiverAccountId);
        CommonSoftAssertions.assertThat(softly).isEqualTo(actualErrorText, INVALID_TRANSFER);
        AccountApiAsserts.assertThatAccount(softly, sender, senderAccountId).hasBalance(INITIAL_BALANCE - MAX_TRANSFER_SUM);

    }

    @Test
    @UserSession(balance = INITIAL_BALANCE)
    @DisplayName("Проверка успешного перевода денежных средств на сторонний аккаунт")
    @ApiVersion(versionApi = "self-transfer-fix")
    public void checkTransferToAlienAccount(TestUser sender, @ExtraUser TestUser receiver) {
        int senderAccountId = sender.accountId();
        int receiverAccountId = receiver.accountId();
        CreateTransferModelResponse response = createTransfer(sender, senderAccountId, MIN_AMOUNT_LIMIT, receiverAccountId);
        assertThatTransfer(softly, response).isSuccessful(MIN_AMOUNT_LIMIT, senderAccountId, receiverAccountId);
        AccountApiAsserts.assertThatAccount(softly, sender, senderAccountId).hasBalance(INITIAL_BALANCE - MIN_AMOUNT_LIMIT);
        AccountApiAsserts.assertThatAccount(softly, receiver, receiverAccountId).hasBalance(MIN_AMOUNT_LIMIT);


    }


    @Test
    @UserSession(balance = INITIAL_BALANCE)
    @DisplayName("Проверка отсутствия возможности перевода на несуществующий аккаунт")
    @ApiVersion(versionApi = "self-transfer-fix")
    public void shouldNotAllowTransferToNotExistAccount(TestUser sender) {
        String actualErrorText = transferExpectingBadRequest(sender, sender.accountId(), MIN_AMOUNT_LIMIT, ACCOUNT_THAT_NOT_EXIST);
        CommonSoftAssertions.assertThat(softly).isEqualTo(actualErrorText, INVALID_TRANSFER);
        AccountApiAsserts.assertThatAccount(softly, sender).hasBalance(INITIAL_BALANCE);
    }

    @Test
    @UserSession(balance = INITIAL_BALANCE)
    @DisplayName("Проверка отсутствия возможности перевода со счета на счет если счет один и тот же")
    @ApiVersion(versionApi = "self-transfer-fix")
    public void shouldNotAllowTransferIfSenderAndReceiverAccountSame(TestUser sender) {
        String actualErrorText = transferExpectingBadRequest(sender, sender.accountId(), MIN_AMOUNT_LIMIT, sender.accountId());
        CommonSoftAssertions.assertThat(softly).isEqualTo(actualErrorText, INVALID_TRANSFER);
        AccountApiAsserts.assertThatAccount(softly, sender).hasBalance(INITIAL_BALANCE);

    }

}
