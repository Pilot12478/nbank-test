package iteration2.ui;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.Alert;
import steps.AdminSteps;
import steps.DepositSteps;
import steps.UserInfo;

import java.util.Map;
import java.util.stream.Stream;

import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.RequestSpecs.getUserToken;
import static steps.AccountSteps.createAccount;
import static steps.AccountSteps.getAccountBalance;
import static steps.AdminSteps.createUser;
import static utils.Helper.calculateActualBalance;
import static utils.constants.BankLimits.MIN_AMOUNT_LIMIT;

public class TransferUITest {
    public UserInfo userInfo;
    public UserInfo anotherUserInfo;
    public static int userAccount;
    public static String VALID_TRANSFER_SUM = "5000.00";
    public static String OVER_LIMIT_TRANSFER_SUM = "12000.00";
    public static String INVALID_TRANSFER_SUM = "-2000.00";
    public static int INVALID_ACCOUNT = 21212;
    public static double DEPOSIT_SUM = 4000.00;
    private static final double INITIAL_SENDER_BALANCE = DEPOSIT_SUM * 2;
    private static final double INITIAL_RECEIVER_BALANCE = 0;

    public static String OVER_INITIAl_BALANCE_TRANSFER_SUM = String.valueOf(INITIAL_SENDER_BALANCE + MIN_AMOUNT_LIMIT);

    @BeforeAll
    public static void setUp() {
        Configuration.baseUrl = "http://192.168.1.67:3000";
        Configuration.remote = "http://localhost:4444/wd/hub";
        Configuration.browser = "chrome";
        Configuration.browserSize = "1980x1080";
        Configuration.browserCapabilities.setCapability("selenoid:options", Map.of("enableVNC", true, "enableLog", true));
    }

    @BeforeEach
    public void preconditionSetUp() {
        userInfo = createUser();
        userAccount = createAccount(userInfo);
        DepositSteps.depositAccount(userInfo, userAccount, DEPOSIT_SUM);
        DepositSteps.depositAccount(userInfo, userAccount, DEPOSIT_SUM);
        String token = getUserToken(userInfo);
        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken',arguments[0]);", token);
        Selenide.open("/transfer");
    }

    @AfterEach
    public void deleteUserAccount() {
        AdminSteps.deleteUser(userInfo);
        AdminSteps.deleteUser(anotherUserInfo);
        Selenide.closeWebDriver();
    }

    public static Stream<Arguments> testDataForNegativeTestsWithInvalidTransferSum() {
        return Stream.of(
                Arguments.of(OVER_LIMIT_TRANSFER_SUM, "❌ Error: Transfer amount cannot exceed 10000", "Сумма перевода превышает лимит"),
                Arguments.of(OVER_INITIAl_BALANCE_TRANSFER_SUM, "❌ Error: Invalid transfer: insufficient funds or invalid accounts",
                        "Сумма перевода больше баланса отправителя"),
                Arguments.of(INVALID_TRANSFER_SUM, "❌ Error: Transfer amount must be at least 0.01", "Сумма перевода отрицательная")
        );
    }

    @Test
    @DisplayName("Проверка успешного перевода пользователем с максимальным количеством параметров(ME-TO-ME)")
    public void shouldBeSuccessTransferMeToMeTestWithMaxParameters() {
        int receiverAccountId = createAccount(userInfo);
        $("select.account-selector").selectOptionContainingText("ACC" + userAccount);
        $(Selectors.byAttribute("placeholder", "Enter recipient name")).setValue("Ivan");
        $(Selectors.byAttribute("placeholder", "Enter recipient account number")).setValue("ACC" + receiverAccountId);
        $(Selectors.byAttribute("placeholder", "Enter amount")).setValue(VALID_TRANSFER_SUM);
        $("#confirmCheck").click();
        $(Selectors.byTagAndText("button", "\uD83D\uDE80 Send Transfer")).click();

        Alert alert = switchTo().alert();
        assertThat(alert.getText()).contains("✅ Successfully transferred " + "$" + VALID_TRANSFER_SUM + " to account " + "ACC" + receiverAccountId + "!");
        alert.accept();
        Selenide.refresh();
        double actualSenderBalance = Double.parseDouble(calculateActualBalance(userAccount));
        double actualReceiverBalance = Double.parseDouble(calculateActualBalance(receiverAccountId));
        double transferSum = Double.parseDouble(VALID_TRANSFER_SUM);

        assertThat(actualSenderBalance).isEqualTo(INITIAL_SENDER_BALANCE - transferSum);
        assertThat(actualReceiverBalance).isEqualTo(transferSum);


        assertThat(getAccountBalance(userInfo, userAccount)).isEqualTo(INITIAL_SENDER_BALANCE - transferSum);
        assertThat(getAccountBalance(userInfo, receiverAccountId)).isEqualTo(transferSum);


    }

    @Test
    @DisplayName("Проверка успешного перевода пользователем с минимальным количеством параметров на аккаунт другого пользователя")
    public void shouldBeSuccessTransferToAnotherUserAccountWithMinParameters() {
        anotherUserInfo = createUser();
        int anotherUserAccount = createAccount(anotherUserInfo);
        $("select.account-selector").selectOptionContainingText("ACC" + userAccount);
        $(Selectors.byAttribute("placeholder", "Enter recipient account number")).setValue("ACC" + anotherUserAccount);
        $(Selectors.byAttribute("placeholder", "Enter amount")).setValue(VALID_TRANSFER_SUM);
        $("#confirmCheck").click();
        $(Selectors.byTagAndText("button", "\uD83D\uDE80 Send Transfer")).click();

        Alert alert = switchTo().alert();
        assertThat(alert.getText()).contains("✅ Successfully transferred " + "$" + VALID_TRANSFER_SUM + " to account " + "ACC" + anotherUserAccount + "!");
        alert.accept();
        Selenide.refresh();
        double actualSenderBalance = Double.parseDouble(calculateActualBalance(userAccount));
        String token = getUserToken(anotherUserInfo);
        executeJavaScript("localStorage.setItem('authToken',arguments[0]);", token);
        Selenide.open("/transfer");
        double actualReceiverBalance = Double.parseDouble(calculateActualBalance(anotherUserAccount));
        double transferSum = Double.parseDouble(VALID_TRANSFER_SUM);

        assertThat(actualSenderBalance).isEqualTo(INITIAL_SENDER_BALANCE - transferSum);
        assertThat(actualReceiverBalance).isEqualTo(transferSum);


        assertThat(getAccountBalance(userInfo, userAccount)).isEqualTo(INITIAL_SENDER_BALANCE - transferSum);
        assertThat(getAccountBalance(anotherUserInfo, anotherUserAccount)).isEqualTo(transferSum);


    }

    @ParameterizedTest(name = "{2}")
    @MethodSource("testDataForNegativeTestsWithInvalidTransferSum")
    public void shouldBeNegativeTransfer(String sum, String errorText, String message) {
        int receiverAccountId = createAccount(userInfo);
        $("select.account-selector").selectOptionContainingText("ACC" + userAccount);
        $(Selectors.byAttribute("placeholder", "Enter recipient name")).setValue("Ivan");
        $(Selectors.byAttribute("placeholder", "Enter recipient account number")).setValue("ACC" + receiverAccountId);
        $(Selectors.byAttribute("placeholder", "Enter amount")).setValue(sum);
        $("#confirmCheck").click();
        $(Selectors.byTagAndText("button", "\uD83D\uDE80 Send Transfer")).click();

        Alert alert = switchTo().alert();
        assertThat(alert.getText()).contains(errorText);
        alert.accept();
        Selenide.refresh();
        double actualSenderBalance = Double.parseDouble(calculateActualBalance(userAccount));
        double actualReceiverBalance = Double.parseDouble(calculateActualBalance(receiverAccountId));

        assertThat(actualSenderBalance).isEqualTo(INITIAL_SENDER_BALANCE);
        assertThat(actualReceiverBalance).isEqualTo(INITIAL_RECEIVER_BALANCE);


        assertThat(getAccountBalance(userInfo, userAccount)).isEqualTo(INITIAL_SENDER_BALANCE);
        assertThat(getAccountBalance(userInfo, receiverAccountId)).isEqualTo(INITIAL_RECEIVER_BALANCE);


    }

    @Test
    @DisplayName("Проверка негативного сценария: указан несуществующий аккаунт в качестве получателя")
    public void shouldBeNegativeWithInvalidAccountReceiver() {
        $("select.account-selector").selectOptionContainingText("ACC" + userAccount);
        $(Selectors.byAttribute("placeholder", "Enter recipient name")).setValue("Ivan");
        $(Selectors.byAttribute("placeholder", "Enter recipient account number")).setValue("ACC" + INVALID_ACCOUNT);
        $(Selectors.byAttribute("placeholder", "Enter amount")).setValue(VALID_TRANSFER_SUM);
        $("#confirmCheck").click();
        $(Selectors.byTagAndText("button", "\uD83D\uDE80 Send Transfer")).click();

        Alert alert = switchTo().alert();
        assertThat(alert.getText()).contains("❌ No user found with this account number.");
        alert.accept();
        Selenide.refresh();
        double actualSenderBalance = Double.parseDouble(calculateActualBalance(userAccount));

        assertThat(actualSenderBalance).isEqualTo(INITIAL_SENDER_BALANCE);

        assertThat(getAccountBalance(userInfo, userAccount)).isEqualTo(INITIAL_SENDER_BALANCE);


    }

    @Test
    @DisplayName("Проверка отсутствия возможности перевода на собственный аккаунт")
    @Disabled("Причина падения: баг на стороне бека"
    )
    public void shouldBeNegativeWithAccSenderEqualsAccReceiver() {
        $("select.account-selector").selectOptionContainingText("ACC" + userAccount);
        $(Selectors.byAttribute("placeholder", "Enter recipient name")).setValue("Ivan");
        $(Selectors.byAttribute("placeholder", "Enter recipient account number")).setValue("ACC" + userAccount);
        $(Selectors.byAttribute("placeholder", "Enter amount")).setValue(VALID_TRANSFER_SUM);
        $("#confirmCheck").click();
        $(Selectors.byTagAndText("button", "\uD83D\uDE80 Send Transfer")).click();

        Alert alert = switchTo().alert();
        assertThat(alert.getText()).contains("❌ Error: Invalid transfer: insufficient funds or invalid accounts");
        alert.accept();
        Selenide.refresh();
        double actualSenderBalance = Double.parseDouble(calculateActualBalance(userAccount));

        assertThat(actualSenderBalance).isEqualTo(INITIAL_SENDER_BALANCE);

        assertThat(getAccountBalance(userInfo, userAccount)).isEqualTo(INITIAL_SENDER_BALANCE);


    }

}
