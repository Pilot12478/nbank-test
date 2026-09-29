package utils;

import generators.RandomModelGenerator;
import models.UpdateUserNameModelRequest;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;
import java.util.random.RandomGenerator;

public class Helper {
    public static String generateName() {
        return RandomModelGenerator.generate(UpdateUserNameModelRequest.class).getName();
    }

    public static String generateInvalidName() {
        return RandomStringUtils.randomAlphabetic(10);
    }
}
