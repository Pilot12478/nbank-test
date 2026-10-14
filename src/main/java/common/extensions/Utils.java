package common.extensions;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import org.awaitility.Awaitility;
import org.openqa.selenium.StaleElementReferenceException;

import java.time.Duration;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;

public class Utils {
    public static void waitForStable(SelenideElement element,
                                     Duration stableFor,
                                     Duration timeout) {
        Awaitility.await()
                .pollInSameThread()
                .pollInterval(stableFor)
                .atMost(timeout)
                .ignoreExceptionsMatching(e ->
                        e instanceof StaleElementReferenceException
                                || e instanceof NoSuchElementException
                                || e instanceof ElementNotFound)
                .until(new Callable<>() {
                    String prev = null;

                    @Override
                    public Boolean call() {
                        String cur = element.getAttribute("outerHTML")
                                + "|" + element.getAttribute("value")
                                + "|" + element.getAttribute("class");
                        boolean stable = cur.equals(prev);
                        prev = cur;
                        return stable;
                    }
                });
    }

}
