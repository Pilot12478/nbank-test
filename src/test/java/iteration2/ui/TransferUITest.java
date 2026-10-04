package iteration2.ui;

import api.steps.AdminSteps;
import api.steps.DepositSteps;
import api.steps.UserInfo;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ui.pages.BankAlert;
import ui.pages.TransferPage;

import java.util.stream.Stream;

import static api.steps.AccountSteps.createAccount;
import static api.steps.AccountSteps.getAccountBalance;
import static api.steps.AdminSteps.createUser;
import static org.assertj.core.api.Assertions.assertThat;
import static utils.Helper.generateInvalidId;
import static utils.Helper.generateName;
import static utils.constants.BankLimits.MIN_AMOUNT_LIMIT;

public class TransferUITest extends BaseUiTest {
    private UserInfo userInfo;
    private UserInfo anotherUserInfo;
    private String receiverName = generateName();
    private int senderAccountId;
    private static final double VALID_TRANSFER_SUM = 5000.00;
    private static final double INVALID_TRANSFER_SUM = 12000.00;
    private static final double INCORRECT_TRANSFER_SUM = -2000.00;
    private static final int INVALID_ACCOUNT = generateInvalidId();
    private static final double DEPOSIT_SUM = 4000.00;
    private static final double INITIAL_SENDER_BALANCE = DEPOSIT_SUM * 2;
    private static final double INITIAL_RECEIVER_BALANCE = 0;
    private static final double OVER_BALANCE_SUM = INITIAL_SENDER_BALANCE + MIN_AMOUNT_LIMIT;


    @BeforeEach
    public void preconditionSetUp() {
        userInfo = createUser();
        senderAccountId = createAccount(userInfo);
        DepositSteps.depositAccount(userInfo, senderAccountId, DEPOSIT_SUM);
        DepositSteps.depositAccount(userInfo, senderAccountId, DEPOSIT_SUM);
        authAsUser(userInfo);
    }

    @AfterEach
    public void deleteUserAccount() {
        Selenide.closeWebDriver();
        AdminSteps.deleteUser(userInfo);
        AdminSteps.deleteUser(anotherUserInfo);
    }

    public static Stream<Arguments> testDataForNegativeTestsWithInvalidTransferSum() {
        return Stream.of(
                Arguments.of(INVALID_TRANSFER_SUM, BankAlert.OVER_LIMIT_TRANSFER_AMOUNT, "Сумма перевода превышает лимит"),
                Arguments.of(OVER_BALANCE_SUM, BankAlert.TRANSFER_SUM_OVER_BALANCE,
                        "Сумма перевода больше баланса отправителя"),
                Arguments.of(INCORRECT_TRANSFER_SUM, BankAlert.INVALID_TRANSFER_AMOUNT, "Сумма перевода отрицательная")
        );
    }

    @Test
    @DisplayName("Проверка успешного перевода пользователем с максимальным количеством параметров(ME-TO-ME)")
    public void shouldBeSuccessTransferMeToMeTestWithMaxParameters() {
        int receiverAccountId = createAccount(userInfo);
        new TransferPage()
                .open()
                .transfer(receiverName, senderAccountId, receiverAccountId, VALID_TRANSFER_SUM)
                .checkAlertMessageAndAccept(BankAlert.TRANSFERRED_SUCCESSFULLY, VALID_TRANSFER_SUM, receiverAccountId)
                .refresh()
                .checkAccountBalance(senderAccountId, INITIAL_SENDER_BALANCE - VALID_TRANSFER_SUM)
                .checkAccountBalance(receiverAccountId, VALID_TRANSFER_SUM);

        assertThat(getAccountBalance(userInfo, senderAccountId)).isEqualTo(INITIAL_SENDER_BALANCE - VALID_TRANSFER_SUM);
        assertThat(getAccountBalance(userInfo, receiverAccountId)).isEqualTo(VALID_TRANSFER_SUM);


    }

    @Test
    @DisplayName("Проверка успешного перевода пользователем с минимальным количеством параметров на аккаунт другого пользователя")
    public void shouldBeSuccessTransferToAnotherUserAccountWithMinParameters() {
        anotherUserInfo = createUser();
        int anotherUserAccount = createAccount(anotherUserInfo);
        new TransferPage()
                .open()
                .transfer(senderAccountId, anotherUserAccount, VALID_TRANSFER_SUM)
                .checkAlertMessageAndAccept(BankAlert.TRANSFERRED_SUCCESSFULLY, VALID_TRANSFER_SUM, anotherUserAccount)
                .refresh()
                .checkAccountBalance(senderAccountId, INITIAL_SENDER_BALANCE - VALID_TRANSFER_SUM);

        authAsUser(anotherUserInfo);
        new TransferPage()
                .open()
                .checkAccountBalance(anotherUserAccount, VALID_TRANSFER_SUM);
        assertThat(getAccountBalance(userInfo, senderAccountId)).isEqualTo(INITIAL_SENDER_BALANCE - VALID_TRANSFER_SUM);
        assertThat(getAccountBalance(anotherUserInfo, anotherUserAccount)).isEqualTo(VALID_TRANSFER_SUM);
    }


    @ParameterizedTest(name = "{2}")
    @MethodSource("testDataForNegativeTestsWithInvalidTransferSum")
    public void shouldBeNegativeTransfer(double sum, BankAlert bankAlert, String message) {
        int receiverAccountId = createAccount(userInfo);
        new TransferPage()
                .open()
                .transfer(senderAccountId, receiverAccountId, sum)
                .checkAlertMessageAndAccept(bankAlert)
                .refresh()
                .checkAccountBalance(senderAccountId, INITIAL_SENDER_BALANCE)
                .checkAccountBalance(receiverAccountId, INITIAL_RECEIVER_BALANCE);

        assertThat(getAccountBalance(userInfo, senderAccountId)).isEqualTo(INITIAL_SENDER_BALANCE);
        assertThat(getAccountBalance(userInfo, receiverAccountId)).isEqualTo(INITIAL_RECEIVER_BALANCE);

    }

    @Test
    @DisplayName("Проверка негативного сценария: указан несуществующий аккаунт в качестве получателя")
    public void shouldBeNegativeWithInvalidAccountReceiver() {
        new TransferPage()
                .open()
                .transfer(senderAccountId, INVALID_ACCOUNT, VALID_TRANSFER_SUM)
                .checkAlertMessageAndAccept(BankAlert.ACCOUNT_NOT_EXIST)
                .refresh()
                .checkAccountBalance(senderAccountId, INITIAL_SENDER_BALANCE);

        assertThat(getAccountBalance(userInfo, senderAccountId)).isEqualTo(INITIAL_SENDER_BALANCE);


    }

    @Test
    @DisplayName("Проверка отсутствия возможности перевода на собственный аккаунт")
    @Disabled("Причина падения: баг на стороне бека"
    )
    public void shouldBeNegativeWithAccSenderEqualsAccReceiver() {
        new TransferPage()
                .open()
                .transfer(senderAccountId, senderAccountId, VALID_TRANSFER_SUM)
                .checkAlertMessageAndAccept(BankAlert.ACCOUNT_RECEIVER_INVALID)
                .refresh()
                .checkAccountBalance(senderAccountId, INITIAL_SENDER_BALANCE);

        assertThat(getAccountBalance(userInfo, senderAccountId)).isEqualTo(INITIAL_SENDER_BALANCE);


    }

}
