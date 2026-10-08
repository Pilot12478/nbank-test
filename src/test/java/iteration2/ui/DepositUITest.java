package iteration2.ui;

import common.annotations.UITest;
import common.annotations.UserSession;
import common.extensions.TestUser;
import iteration2.api.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ui.pages.BankAlert;
import ui.pages.DepositPage;

import java.util.stream.Stream;

import static api.steps.AccountSteps.getAccountBalance;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;
@UITest
public class DepositUITest extends BaseTest {
    private static double VALID_DEPOSIT_SUM = 5000.00;
    private static double OVER_LIMIT_DEPOSIT_SUM = 6000.00;
    private static double INVALID_SUM = -6000.00;
    private static double INITIAL_BALANCE = 0.00;


    public static Stream<Arguments> testDataForNegativeTest() {
        return Stream.of(
                Arguments.of(OVER_LIMIT_DEPOSIT_SUM, BankAlert.OVER_LIMIT_DEPOSIT_AMOUNT, "Сумма депозита превышает лимит 5000"),
                Arguments.of(INVALID_SUM, BankAlert.INVALID_DEPOSIT_AMOUNT, "Сумма депозита имеет отрицательное значение")
        );
    }

    @Test
    @UserSession
    @DisplayName("Проверка успешного пополнения баланса")
    public void depositShouldBeSuccessTest(TestUser user) {
        new DepositPage()
                .open()
                .deposit(VALID_DEPOSIT_SUM, user.accounts().getFirst())
                .checkAlertMessageAndAccept(BankAlert.DEPOSITED_SUCCESSFULLY, VALID_DEPOSIT_SUM, user.accounts().getFirst())
                .open().checkAccountBalance(user.accounts().getFirst(), VALID_DEPOSIT_SUM);
        assertThat(getAccountBalance(user.user(), user.accounts().getFirst())).isCloseTo(VALID_DEPOSIT_SUM, offset(0.001));


    }

    @ParameterizedTest(name = "{2}: {0}")
    @MethodSource("testDataForNegativeTest")
    @UserSession
    public void shouldNotAllowDeposit(double sum, BankAlert bankAlert, String message, TestUser user) {
        new DepositPage()
                .open()
                .deposit(sum, user.accounts().getFirst())
                .checkAlertMessageAndAccept(bankAlert)
                .refresh()
                .checkAccountBalance(user.accounts().getFirst(), INITIAL_BALANCE);
        assertThat(getAccountBalance(user.user(), user.accounts().getFirst())).isCloseTo(INITIAL_BALANCE, offset(0.001));


    }

}
