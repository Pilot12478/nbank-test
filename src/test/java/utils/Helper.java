package utils;

import generators.RandomModelGenerator;
import models.UpdateUserNameModelRequest;

public class Helper {
    public static String generateName() {
        return RandomModelGenerator.generate(UpdateUserNameModelRequest.class).getName();
    }
}
