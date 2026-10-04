package iteration2.ui;

import api.steps.AccountSteps;
import api.steps.AdminSteps;
import api.steps.UserInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ui.pages.BankAlert;
import ui.pages.DepositPage;
import ui.pages.UserDashboardPage;

import java.util.stream.Stream;

import static api.steps.AccountSteps.getAccountBalance;
import static api.steps.AdminSteps.createUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;


public class DepositUITest extends BaseUiTest {
    private UserInfo userInfo;
    private int accId;
    private static double VALID_DEPOSIT_SUM = 5000.00;
    private static double OVER_LIMIT_DEPOSIT_SUM = 6000.00;
    private static double INVALID_SUM = -6000.00;
    private static double INITIAL_BALANCE = 0.00;


    @BeforeEach
    public void preconditionSetUp() {
        userInfo = createUser();
        accId = AccountSteps.createAccount(userInfo);
        authAsUser(userInfo);

    }

    @AfterEach
    public void deleteUserAccount() {
        AdminSteps.deleteUser(userInfo);
    }

    public static Stream<Arguments> testDataForNegativeTest() {
        return Stream.of(
                Arguments.of(OVER_LIMIT_DEPOSIT_SUM, BankAlert.OVER_LIMIT_DEPOSIT_AMOUNT, "Сумма депозита превышает лимит 5000"),
                Arguments.of(INVALID_SUM, BankAlert.INVALID_DEPOSIT_AMOUNT, "Сумма депозита имеет отрицательное значение")
        );
    }

    @Test
    @DisplayName("Проверка успешного пополнения баланса")
    public void depositShouldBeSuccessTest() {
        new DepositPage()
                .open()
                .deposit(VALID_DEPOSIT_SUM, accId)
                .checkAlertMessageAndAccept(BankAlert.DEPOSITED_SUCCESSFULLY, VALID_DEPOSIT_SUM, accId)
                .getPage(DepositPage.class)
                .checkAccountBalance(accId, VALID_DEPOSIT_SUM);
        assertThat(getAccountBalance(userInfo, accId)).isCloseTo(VALID_DEPOSIT_SUM, offset(0.001));


    }

    @ParameterizedTest(name = "{2}: {0}")
    @MethodSource("testDataForNegativeTest")
    public void shouldNotAllowDeposit(double sum, BankAlert bankAlert, String message) {
        new DepositPage()
                .open()
                .deposit(sum, accId)
                .checkAlertMessageAndAccept(bankAlert)
                .refresh()
                .checkAccountBalance(accId, INITIAL_BALANCE);
        assertThat(getAccountBalance(userInfo, accId)).isCloseTo(INITIAL_BALANCE, offset(0.001));


    }

}
