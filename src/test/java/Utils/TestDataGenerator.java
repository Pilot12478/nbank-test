package Utils;

import java.util.concurrent.ThreadLocalRandom;

public class TestDataGenerator {
    public static String generateUserName(){
        return "User_"+ThreadLocalRandom.current().nextInt(1,999);
    }

    public static String getDefaultPassword() {
        return "StrongP@ssw0rd123!";
    }
}
