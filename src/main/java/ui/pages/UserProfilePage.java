package ui.pages;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import static com.codeborne.selenide.Condition.interactable;
import static com.codeborne.selenide.Selenide.$;
@Getter
public class UserProfilePage extends BasePage<UserProfilePage> {
    private SelenideElement nameInput = $(Selectors.byAttribute("placeholder", "Enter new name"));
    private SelenideElement name = $(Selectors.byClassName("user-name"));
    private SelenideElement button = $(Selectors.byTagAndText("button","\uD83D\uDCBE Save Changes"));

    @Override
    public String url() {
        return "/edit-profile";
    }
    public UserProfilePage changeName(String name){
        nameInput.shouldBe(interactable).clear();
        nameInput.setValue(name);
        button.click();
        return this;
    }
}
