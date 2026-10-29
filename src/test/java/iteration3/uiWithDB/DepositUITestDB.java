package iteration3.uiWithDB;

import common.annotations.ApiVersion;
import common.annotations.DataBase;
import common.annotations.UITest;
import common.annotations.UiSession;
import common.assertions.AccountApiAsserts;
import common.extensions.TestUser;
import db.asserts.AccountDbAsserts;
import iteration2.api.BaseTest;
import org.jooq.DSLContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ui.pages.BankAlert;
import ui.pages.DepositPage;

import java.util.stream.Stream;

@UITest
@ApiVersion(versionApi = "with_database_with_fix")
public class DepositUITestDB extends BaseTest {
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
    @UiSession
    @DisplayName("Проверка успешного пополнения баланса")
    public void depositShouldBeSuccessTest(TestUser user, @DataBase DSLContext dslContext) {
        new DepositPage()
                .open()
                .deposit(VALID_DEPOSIT_SUM, user.accountId())
                .checkAlertMessageAndAccept(BankAlert.DEPOSITED_SUCCESSFULLY, VALID_DEPOSIT_SUM, user.accountId())
                .open().checkAccountBalance(user.accountId(), VALID_DEPOSIT_SUM);
        AccountApiAsserts.assertThatAccount(softly, user).hasBalance(VALID_DEPOSIT_SUM);
        AccountDbAsserts.assertThatAccount(softly, dslContext, user.accountId()).hasBalance(VALID_DEPOSIT_SUM);


    }

    @ParameterizedTest(name = "{2}: {0}")
    @MethodSource("testDataForNegativeTest")
    @UiSession
    public void shouldNotAllowDeposit(double sum, BankAlert bankAlert, String message, TestUser user, @DataBase DSLContext dslContext) {
        new DepositPage()
                .open()
                .deposit(sum, user.accountId())
                .checkAlertMessageAndAccept(bankAlert)
                .refresh()
                .checkAccountBalance(user.accountId(), INITIAL_BALANCE);
        AccountApiAsserts.assertThatAccount(softly, user).hasBalance(INITIAL_BALANCE);
        AccountDbAsserts.assertThatAccount(softly, dslContext, user.accountId()).hasBalance(INITIAL_BALANCE);


    }
}
