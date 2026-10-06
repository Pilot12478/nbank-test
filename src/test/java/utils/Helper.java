package utils;

import generators.RandomModelGenerator;
import models.LoginUserModelRequest;
import models.UpdateUserNameModelRequest;
import org.apache.commons.lang3.RandomStringUtils;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;
import steps.UserInfo;

import java.util.Random;
import java.util.random.RandomGenerator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$$;

public class Helper {
    public static String generateName() {
        return RandomModelGenerator.generate(UpdateUserNameModelRequest.class).getName();
    }

    public static String generateInvalidName() {
        return RandomStringUtils.randomAlphabetic(10);
    }

    public static String calculateActualBalance(int accountId) {

        String optionText = $$("select.account-selector option")
                .findBy(text("ACC"+accountId))
                .getText();
        Pattern pattern = Pattern.compile("Balance: \\$([0-9]+\\.[0-9]{2})");
        Matcher matcher = pattern.matcher(optionText);

        if (!matcher.find()) {
            throw new IllegalStateException(
                    "Не удалось распарсить баланс из текста опции: " + optionText);
        }
        return matcher.group(1);
    }


}
