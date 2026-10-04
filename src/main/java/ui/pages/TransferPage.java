package ui.pages;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import static com.codeborne.selenide.Selenide.$;

@Getter
public class TransferPage extends BasePage<TransferPage> {
    private SelenideElement recipientName = $(Selectors.byAttribute("placeholder", "Enter recipient name"));
    private SelenideElement recipientAccountNumber = $(Selectors.byAttribute("placeholder", "Enter recipient account number"));
    private SelenideElement confirmCheckBox = $("#confirmCheck");
    private SelenideElement transferButton = $(Selectors.byTagAndText("button", "\uD83D\uDE80 Send Transfer"));

    @Override
    public String url() {
        return "/transfer";
    }

    public TransferPage transfer(String recipientName, int senderAccountNumber, int recipientAccountNumber, double amount) {
        this.recipientName.setValue(recipientName);
        this.accountSelector.selectOptionContainingText("ACC" + senderAccountNumber);
        this.recipientAccountNumber.setValue("ACC" + recipientAccountNumber);
        this.amount.setValue(String.valueOf(amount));
        this.confirmCheckBox.click();
        this.transferButton.click();
        return this;

    }

    public TransferPage transfer(int senderAccountNumber, int recipientAccountNumber, double amount) {
        return transfer(null, senderAccountNumber, recipientAccountNumber, amount);

    }


}
