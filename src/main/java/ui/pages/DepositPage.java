package ui.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@Getter
public class DepositPage extends BasePage<DepositPage>{
    private SelenideElement button =  $(byText("\uD83D\uDCB5 Deposit"));
    @Override
    public String url() {
        return "/deposit";
    }

    public DepositPage deposit(double amount, int accId){
       this.accountSelector.selectOptionContainingText("ACC"+accId);
        this.amount.setValue(String.valueOf(amount));
        this.button.click();
        return this;
    }
}
