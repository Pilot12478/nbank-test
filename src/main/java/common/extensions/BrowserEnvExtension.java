package common.extensions;

import api.configs.Config;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.Map;

public class BrowserEnvExtension implements BeforeAllCallback, AfterEachCallback {
    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        Configuration.baseUrl = Config.uiBaseUrl();
        Configuration.remote = Config.remote();
        Configuration.browser = Config.browser();
        Configuration.browserSize = Config.browserSize();
        Configuration.browserCapabilities.setCapability("selenoid:options", Map.of("enableVNC", true, "enableLog", true));
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        Selenide.closeWebDriver();

    }

}
