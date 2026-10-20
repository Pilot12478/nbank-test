package common.extensions;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import org.awaitility.Awaitility;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;

import java.time.Duration;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;

public class Utils {
    public static void waitTillRequestEnd() {
        Selenide.Wait().until(driver -> {
            Long active = (Long) ((JavascriptExecutor) driver).executeScript(
                    "return window.performance.getEntriesByType('resource')" +
                            ".filter(r => r.initiatorType === 'xmlhttprequest' && !r.responseEnd).length"
            );
            return active == 0L;
        });

    }

}
