package ui.pages;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import static com.codeborne.selenide.Selenide.$;
@Getter
public class UserDashboardPage extends BasePage<UserDashboardPage> {
    private SelenideElement depositButton = $(Selectors.byTagAndText("button", "\uD83D\uDCB0 Deposit Money"));
    private SelenideElement transferButton = $(Selectors.byTagAndText("button", "\uD83D\uDD04 Make a Transfer"));
    private SelenideElement accountButton = $(Selectors.byTagAndText("button", "➕ Create New Account"));

    @Override
    public String url() {
        return "/dashboard";
    }
}
