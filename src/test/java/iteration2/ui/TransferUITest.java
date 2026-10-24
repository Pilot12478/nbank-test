package iteration2.ui;

import common.annotations.ApiVersion;
import common.annotations.ExtraUser;
import common.annotations.UITest;
import common.annotations.UiSession;
import common.assertions.AccountApiAsserts;
import common.extensions.TestUser;
import iteration2.api.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ui.pages.BankAlert;
import ui.pages.TransferPage;

import java.util.stream.Stream;

import static ui.pages.BasePage.authAsUser;
import static utils.Helper.generateInvalidId;
import static utils.Helper.generateName;
import static utils.constants.BankLimits.MIN_AMOUNT_LIMIT;
@UITest
public class TransferUITest extends BaseTest {
    private String receiverName = generateName();
    private static final double VALID_TRANSFER_SUM = 5000.00;
    private static final double INVALID_TRANSFER_SUM = 12000.00;
    private static final double INCORRECT_TRANSFER_SUM = -2000.00;
    private static final int INVALID_ACCOUNT = generateInvalidId();
    private static final double DEPOSIT_SUM = 4000.00;
    private static final double INITIAL_SENDER_BALANCE = DEPOSIT_SUM * 2;
    private static final double INITIAL_RECEIVER_BALANCE = 0;
    private static final double OVER_BALANCE_SUM = INITIAL_SENDER_BALANCE + MIN_AMOUNT_LIMIT;

    public static Stream<Arguments> testDataForNegativeTestsWithInvalidTransferSum() {
        return Stream.of(
                Arguments.of(INVALID_TRANSFER_SUM, BankAlert.OVER_LIMIT_TRANSFER_AMOUNT, "Сумма перевода превышает лимит"),
                Arguments.of(OVER_BALANCE_SUM, BankAlert.TRANSFER_SUM_OVER_BALANCE,
                        "Сумма перевода больше баланса отправителя"),
                Arguments.of(INCORRECT_TRANSFER_SUM, BankAlert.INVALID_TRANSFER_AMOUNT, "Сумма перевода отрицательная")
        );
    }

    @Test
    @UiSession(accounts = 2, balance = INITIAL_SENDER_BALANCE)
    @DisplayName("Проверка успешного перевода пользователем с максимальным количеством параметров(ME-TO-ME)")
    @ApiVersion(versionApi = "self-transfer-fix")
    public void shouldBeSuccessTransferMeToMeTestWithMaxParameters(TestUser user) {
        int senderAccountId = user.accountId();
        int receiverAccountId = user.accountId(1);
        new TransferPage()
                .open()
                .transfer(receiverName, senderAccountId, receiverAccountId, VALID_TRANSFER_SUM)
                .checkAlertMessageAndAccept(BankAlert.TRANSFERRED_SUCCESSFULLY, VALID_TRANSFER_SUM, receiverAccountId)
                .refresh()
                .checkAccountBalance(senderAccountId, INITIAL_SENDER_BALANCE - VALID_TRANSFER_SUM)
                .checkAccountBalance(receiverAccountId, VALID_TRANSFER_SUM);

        AccountApiAsserts.assertThatAccount(softly, user, senderAccountId).hasBalance(INITIAL_SENDER_BALANCE - VALID_TRANSFER_SUM);
        AccountApiAsserts.assertThatAccount(softly, user, receiverAccountId).hasBalance(VALID_TRANSFER_SUM);


    }

    @Test
    @UiSession(balance = INITIAL_SENDER_BALANCE)
    @DisplayName("Проверка успешного перевода пользователем с минимальным количеством параметров на аккаунт другого пользователя")
    @ApiVersion(versionApi = "self-transfer-fix")
    public void shouldBeSuccessTransferToAnotherUserAccountWithMinParameters(TestUser sender, @ExtraUser TestUser receiver) {
        int senderAccountId = sender.accountId();
        int receiverAccountId = receiver.accountId();
        new TransferPage()
                .open()
                .transfer(senderAccountId, receiverAccountId, VALID_TRANSFER_SUM)
                .checkAlertMessageAndAccept(BankAlert.TRANSFERRED_SUCCESSFULLY, VALID_TRANSFER_SUM, receiverAccountId)
                .refresh()
                .checkAccountBalance(senderAccountId, INITIAL_SENDER_BALANCE - VALID_TRANSFER_SUM);
        authAsUser(receiver.credentials());
        new TransferPage()
                .open()
                .checkAccountBalance(receiverAccountId, VALID_TRANSFER_SUM);
        AccountApiAsserts.assertThatAccount(softly, sender, senderAccountId).hasBalance(INITIAL_SENDER_BALANCE - VALID_TRANSFER_SUM);
        AccountApiAsserts.assertThatAccount(softly, receiver, receiverAccountId).hasBalance(VALID_TRANSFER_SUM);
    }


    @ParameterizedTest(name = "{2}")
    @UiSession(accounts = 2, balance = INITIAL_SENDER_BALANCE)
    @MethodSource("testDataForNegativeTestsWithInvalidTransferSum")
    @ApiVersion(versionApi = "self-transfer-fix")
    public void shouldBeNegativeTransfer(double sum, BankAlert bankAlert, String message, TestUser sender) {
        int senderAccountId = sender.accountId();
        int receiverAccountId = sender.accountId(1);
        new TransferPage()
                .open()
                .transfer(senderAccountId, receiverAccountId, sum)
                .checkAlertMessageAndAccept(bankAlert)
                .refresh()
                .checkAccountBalance(senderAccountId, INITIAL_SENDER_BALANCE)
                .checkAccountBalance(receiverAccountId, INITIAL_RECEIVER_BALANCE);

        AccountApiAsserts.assertThatAccount(softly, sender, senderAccountId).hasBalance(INITIAL_SENDER_BALANCE);
        AccountApiAsserts.assertThatAccount(softly, sender, receiverAccountId).hasBalance(INITIAL_RECEIVER_BALANCE);

    }

    @Test
    @UiSession(balance = INITIAL_SENDER_BALANCE)
    @DisplayName("Проверка негативного сценария: указан несуществующий аккаунт в качестве получателя")
    @ApiVersion(versionApi = "self-transfer-fix")
    public void shouldBeNegativeWithInvalidAccountReceiver(TestUser sender) {
        int senderAccountId = sender.accountId();
        new TransferPage()
                .open()
                .transfer(senderAccountId, INVALID_ACCOUNT, VALID_TRANSFER_SUM)
                .checkAlertMessageAndAccept(BankAlert.ACCOUNT_NOT_EXIST)
                .refresh()
                .checkAccountBalance(senderAccountId, INITIAL_SENDER_BALANCE);

        AccountApiAsserts.assertThatAccount(softly, sender, senderAccountId).hasBalance(INITIAL_SENDER_BALANCE);


    }

    @Test
    @UiSession(balance = INITIAL_SENDER_BALANCE)
    @DisplayName("Проверка отсутствия возможности перевода на собственный аккаунт")
    @ApiVersion(versionApi = "self-transfer-fix")
    public void shouldBeNegativeWithAccSenderEqualsAccReceiver(TestUser sender) {
        int senderAccountId = sender.accountId();
        new TransferPage()
                .open()
                .transfer(senderAccountId, senderAccountId, VALID_TRANSFER_SUM)
                .checkAlertMessageAndAccept(BankAlert.ACCOUNT_RECEIVER_INVALID)
                .refresh()
                .checkAccountBalance(senderAccountId, INITIAL_SENDER_BALANCE);

        AccountApiAsserts.assertThatAccount(softly, sender, senderAccountId).hasBalance(INITIAL_SENDER_BALANCE);


    }

}
