package iteration2.ui;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.Alert;
import steps.AccountSteps;
import steps.AdminSteps;
import steps.UserInfo;

import java.util.Map;
import java.util.stream.Stream;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.RequestSpecs.getUserToken;
import static steps.AccountSteps.getAccountBalance;
import static utils.Helper.calculateActualBalance;


public class DepositUITest {
    private UserInfo userInfo;
    private int accId;
    public static String VALID_DEPOSIT_SUM = "5000.00";
    public static String OVER_LIMIT_DEPOSIT_SUM = "6000";
    public static String INVALID_SUM = "-6000";
    public static String INITIAL_BALANCE = "0.00";

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
        userInfo = AdminSteps.createUser();
        accId = AccountSteps.createAccount(userInfo);
        String token = getUserToken(userInfo);
        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken',arguments[0]);", token);
        Selenide.open("/deposit");
        $(Selectors.byCssSelector("select.account-selector")).selectOptionContainingText("ACC" + accId);
    }
    @AfterEach
    public void deleteUserAccount() {
        AdminSteps.deleteUser(userInfo);
    }

    public static Stream<Arguments> testDataForNegativeTest() {
        return Stream.of(
                Arguments.of(OVER_LIMIT_DEPOSIT_SUM, "❌ Please deposit less or equal to 5000$.", "Сумма депозита превышает лимит 5000"),
                Arguments.of(INVALID_SUM, "❌ Please enter a valid amount.", "Сумма депозита имеет отрицательное значение")
        );
    }

    @Test
    @DisplayName("Проверка успешного пополнения баланса")
    public void depositShouldBeSuccessTest() {
        $(Selectors.byAttribute("placeholder", "Enter amount")).setValue(VALID_DEPOSIT_SUM);
        $(byText("\uD83D\uDCB5 Deposit")).click();
        Alert alert = switchTo().alert();
        assertThat(alert.getText()).contains("✅ Successfully deposited " + "$" + VALID_DEPOSIT_SUM + " to account " + "ACC" + accId + "!");
        alert.accept();
        Selenide.open("/deposit");
        $("select.account-selector").click();
        String actualBalance = calculateActualBalance(accId);
        System.out.println(actualBalance);
        assertThat(actualBalance).isEqualTo(VALID_DEPOSIT_SUM);

        Double expBalance = Double.parseDouble(VALID_DEPOSIT_SUM);

        assertThat(getAccountBalance(userInfo, accId)).isEqualTo(expBalance);


    }

    @ParameterizedTest(name = "{2}: {0}")
    @MethodSource("testDataForNegativeTest")
    public void shouldNotAllowDeposit(String sum, String errorText, String message) {
        $(Selectors.byAttribute("placeholder", "Enter amount")).setValue(sum);
        $(byText("\uD83D\uDCB5 Deposit")).click();
        Alert alert = switchTo().alert();
        assertThat(alert.getText()).contains(errorText);
        alert.accept();
        Selenide.open("/deposit");
        $("select.account-selector").click();

        String actualBalance = calculateActualBalance(accId);
        assertThat(actualBalance).isEqualTo(INITIAL_BALANCE);

        Double expBalance = Double.parseDouble(INITIAL_BALANCE);

        assertThat(getAccountBalance(userInfo, accId)).isEqualTo(expBalance);


    }

}
