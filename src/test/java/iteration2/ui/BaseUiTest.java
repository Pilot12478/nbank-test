package iteration2.ui;

import api.configs.Config;
import api.steps.UserInfo;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import iteration2.api.BaseTest;
import org.junit.jupiter.api.BeforeAll;

import java.util.Map;

import static api.specs.RequestSpecs.getUserToken;
import static com.codeborne.selenide.Selenide.executeJavaScript;

public class BaseUiTest extends BaseTest {
    @BeforeAll
    public static void setUp() {
        Configuration.baseUrl = Config.getProperty("baseUIUrl");
        Configuration.remote = Config.getProperty("remote");
        Configuration.browser = Config.getProperty("browser");
        Configuration.browserSize = Config.getProperty("browserSize");
        Configuration.browserCapabilities.setCapability("selenoid:options", Map.of("enableVNC", true, "enableLog", true));
    }

    public void authAsUser(UserInfo userInfo) {
        String token = getUserToken(userInfo);
        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken',arguments[0]);", token);

    }
}
