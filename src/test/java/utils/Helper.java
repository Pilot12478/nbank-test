package utils;

import api.generators.RandomModelGenerator;
import api.models.CreateUserModelRequest;
import api.models.UpdateUserNameModelRequest;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.concurrent.ThreadLocalRandom;

public class Helper {
    public static String generateName() {
        return RandomModelGenerator.generate(UpdateUserNameModelRequest.class).getName();
    }

    public static String generateInvalidName() {
        return RandomStringUtils.randomAlphabetic(10);
    }

    public static int generateInvalidId() {
        return ThreadLocalRandom.current().nextInt(100, Integer.MAX_VALUE);
    }


}
