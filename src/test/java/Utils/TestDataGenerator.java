package Utils;

import org.apache.commons.lang3.RandomStringUtils;


public class TestDataGenerator {
    public static String generateUserName() {
        return RandomStringUtils.randomAlphabetic(10);
    }

    public static String getDefaultPassword() {
        return RandomStringUtils.randomAlphabetic(3).toLowerCase() +
                RandomStringUtils.randomAlphabetic(3).toUpperCase() +
                RandomStringUtils.randomNumeric(3) +
                "@*";
    }
}
