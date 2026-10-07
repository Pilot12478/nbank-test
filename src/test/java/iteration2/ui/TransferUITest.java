package iteration2.ui;

import common.annotations.ExtraUser;
import common.annotations.UITest;
import common.annotations.UserSession;
import common.extensions.TestUser;
import iteration2.api.BaseTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ui.pages.BankAlert;
import ui.pages.TransferPage;

import java.util.stream.Stream;

import static api.steps.AccountSteps.getAccountBalance;
import static org.assertj.core.api.Assertions.assertThat;
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
    @UserSession(accounts = 2, balance = INITIAL_SENDER_BALANCE)
    @DisplayName("Проверка успешного перевода пользователем с максимальным количеством параметров(ME-TO-ME)")
    public void shouldBeSuccessTransferMeToMeTestWithMaxParameters(TestUser user) {
        int senderAccountId = user.accounts().getFirst();
        int receiverAccountId = user.accounts().get(1);
        new TransferPage()
                .open()
                .transfer(receiverName, senderAccountId, receiverAccountId, VALID_TRANSFER_SUM)
                .checkAlertMessageAndAccept(BankAlert.TRANSFERRED_SUCCESSFULLY, VALID_TRANSFER_SUM, receiverAccountId)
                .refresh()
                .checkAccountBalance(senderAccountId, INITIAL_SENDER_BALANCE - VALID_TRANSFER_SUM)
                .checkAccountBalance(receiverAccountId, VALID_TRANSFER_SUM);

        assertThat(getAccountBalance(user.user(), senderAccountId)).isEqualTo(INITIAL_SENDER_BALANCE - VALID_TRANSFER_SUM);
        assertThat(getAccountBalance(user.user(), receiverAccountId)).isEqualTo(VALID_TRANSFER_SUM);


    }

    @Test
    @UserSession(balance = INITIAL_SENDER_BALANCE)
    @DisplayName("Проверка успешного перевода пользователем с минимальным количеством параметров на аккаунт другого пользователя")
    public void shouldBeSuccessTransferToAnotherUserAccountWithMinParameters(TestUser sender, @ExtraUser TestUser receiver) {
        int senderAccountId = sender.accounts().getFirst();
        int receiverAccountId = receiver.accounts().getFirst();
        new TransferPage()
                .open()
                .transfer(senderAccountId, receiverAccountId, VALID_TRANSFER_SUM)
                .checkAlertMessageAndAccept(BankAlert.TRANSFERRED_SUCCESSFULLY, VALID_TRANSFER_SUM, receiverAccountId)
                .refresh()
                .checkAccountBalance(senderAccountId, INITIAL_SENDER_BALANCE - VALID_TRANSFER_SUM);
        authAsUser(receiver.user());
        new TransferPage()
                .open()
                .checkAccountBalance(receiverAccountId, VALID_TRANSFER_SUM);
        assertThat(getAccountBalance(sender.user(), senderAccountId)).isEqualTo(INITIAL_SENDER_BALANCE - VALID_TRANSFER_SUM);
        assertThat(getAccountBalance(receiver.user(), receiverAccountId)).isEqualTo(VALID_TRANSFER_SUM);
    }


    @ParameterizedTest(name = "{2}")
    @UserSession(accounts = 2, balance = INITIAL_SENDER_BALANCE)
    @MethodSource("testDataForNegativeTestsWithInvalidTransferSum")
    public void shouldBeNegativeTransfer(double sum, BankAlert bankAlert, String message, TestUser sender) {
        int senderAccountId = sender.accounts().getFirst();
        int receiverAccountId = sender.accounts().get(1);
        new TransferPage()
                .open()
                .transfer(senderAccountId, receiverAccountId, sum)
                .checkAlertMessageAndAccept(bankAlert)
                .refresh()
                .checkAccountBalance(senderAccountId, INITIAL_SENDER_BALANCE)
                .checkAccountBalance(receiverAccountId, INITIAL_RECEIVER_BALANCE);

        assertThat(getAccountBalance(sender.user(), senderAccountId)).isEqualTo(INITIAL_SENDER_BALANCE);
        assertThat(getAccountBalance(sender.user(), receiverAccountId)).isEqualTo(INITIAL_RECEIVER_BALANCE);

    }

    @Test
    @UserSession(balance = INITIAL_SENDER_BALANCE)
    @DisplayName("Проверка негативного сценария: указан несуществующий аккаунт в качестве получателя")
    public void shouldBeNegativeWithInvalidAccountReceiver(TestUser sender) {
        int senderAccountId = sender.accounts().getFirst();
        new TransferPage()
                .open()
                .transfer(senderAccountId, INVALID_ACCOUNT, VALID_TRANSFER_SUM)
                .checkAlertMessageAndAccept(BankAlert.ACCOUNT_NOT_EXIST)
                .refresh()
                .checkAccountBalance(senderAccountId, INITIAL_SENDER_BALANCE);

        assertThat(getAccountBalance(sender.user(), senderAccountId)).isEqualTo(INITIAL_SENDER_BALANCE);


    }

    @Test
    @UserSession(balance = INITIAL_SENDER_BALANCE)
    @DisplayName("Проверка отсутствия возможности перевода на собственный аккаунт")
    @Disabled("Причина падения: баг на стороне бека"
    )
    public void shouldBeNegativeWithAccSenderEqualsAccReceiver(TestUser sender) {
        int senderAccountId = sender.accounts().getFirst();
        new TransferPage()
                .open()
                .transfer(senderAccountId, senderAccountId, VALID_TRANSFER_SUM)
                .checkAlertMessageAndAccept(BankAlert.ACCOUNT_RECEIVER_INVALID)
                .refresh()
                .checkAccountBalance(senderAccountId, INITIAL_SENDER_BALANCE);

        assertThat(getAccountBalance(sender.user(), senderAccountId)).isEqualTo(INITIAL_SENDER_BALANCE);


    }

}
