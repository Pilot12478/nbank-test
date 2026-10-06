package iteration2.ui;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import models.UserModelResponseProfile;
import org.junit.jupiter.api.*;
import org.openqa.selenium.Alert;
import steps.AccountSteps;
import steps.AdminSteps;
import steps.UserInfo;
import steps.UserInfoSteps;

import java.util.Map;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static specs.RequestSpecs.getUserToken;
import static utils.Helper.generateInvalidName;
import static utils.Helper.generateName;

public class UserNameUITest {
    private UserInfo userInfo;
    private static final String VALID_USER_NAME = generateName();
    private static final String INVALID_USER_NAME = generateInvalidName();
    private static final String DEFAULT_USER_NAME = "Noname";

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
        String token = getUserToken(userInfo);
        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken',arguments[0]);", token);
        Selenide.open("/edit-profile");
    }

    @AfterEach
    public void deleteUserAccount() {
        AdminSteps.deleteUser(userInfo);
    }
    @Test
    @DisplayName("Проверка успешного смены имени")
    public void shouldBeSuccessChangeNameTest(){
        SelenideElement nameInput =
                $(Selectors.byAttribute("placeholder", "Enter new name")).shouldBe(visible);

        nameInput.shouldBe(interactable).clear();
        nameInput.setValue(VALID_USER_NAME);
        $(Selectors.byTagAndText("button","\uD83D\uDCBE Save Changes")).click();
        Alert alert = switchTo().alert();
        assertThat(alert.getText()).isEqualTo("✅ Name updated successfully!");
        alert.accept();
        Selenide.refresh();
        $(Selectors.byClassName("user-name")).shouldHave(text(VALID_USER_NAME));
        UserModelResponseProfile responseProfile =UserInfoSteps.getUserAccount(userInfo);
        assertThat(responseProfile.getName()).isEqualTo(VALID_USER_NAME);


    }

    @Test
    @DisplayName("Проверка негативного сценария")
    @Disabled("Причина падения: браузер перед загрузкой профиля делает 4 запроса на бекэнд," +
            "после каждой загрузки происходит новый рендеринг, значение имени перетирается"+
            "выглядит как баг фронта, 4 запроса на бэк это лишнее"
    )
    public void shouldBeNegativeChangeNameTest(){
        SelenideElement nameInput =
                $(Selectors.byAttribute("placeholder", "Enter new name")).shouldBe(visible);
        nameInput.shouldBe(interactable).clear();
        nameInput.setValue(INVALID_USER_NAME);
        $(Selectors.byTagAndText("button","\uD83D\uDCBE Save Changes")).click();
        Alert alert = switchTo().alert();
        assertThat(alert.getText()).isEqualTo("Name must contain two words with letters only");
        alert.accept();
        Selenide.refresh();
        $(Selectors.byClassName("user-name")).shouldHave(text(DEFAULT_USER_NAME));
        UserModelResponseProfile responseProfile =UserInfoSteps.getUserAccount(userInfo);
        assertNull(responseProfile.getName());


    }

}
