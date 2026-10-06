package ui.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import org.openqa.selenium.Alert;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.assertThat;

@Getter
public abstract class BasePage<T extends BasePage> {
    protected SelenideElement accountSelector = $(Selectors.byCssSelector("select.account-selector"));
    protected SelenideElement amount = $(Selectors.byAttribute("placeholder", "Enter amount"));
    private ElementsCollection options = $$("select.account-selector option");

    public abstract String url();

    public T open() {
        return Selenide.open(url(), (Class<T>) this.getClass());
    }

    public <T extends BasePage> T getPage(Class<T> pageClass) {
        return Selenide.page(pageClass);

    }

    public T checkAlertMessageAndAccept(BankAlert bankAlert, Object... args) {
        String expected = args.length == 0 ? bankAlert.getMessage() : bankAlert.format(args);
        Alert alert = switchTo().alert();
        assertThat(alert.getText()).contains(expected);
        alert.accept();
        return (T) this;
    }

    public T checkAccountBalance(int accId, double expectedBalance) {
        this.options.findBy(text("ACC" + accId))
                .shouldHave(text("Balance: $" + expectedBalance));
        return (T) this;
    }

    public T refresh(){
        Selenide.refresh();
        return (T) this;
    }


}
