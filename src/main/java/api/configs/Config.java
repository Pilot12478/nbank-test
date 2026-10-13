package api.configs;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static final Config INSTANCE = new Config();
    private final Properties properties = new Properties();


    private Config() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("config.properties not found in resources");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Fail to load config.properties", e);
        }
    }

    public static String getProperty(String key) {
        return INSTANCE.properties.getProperty(key);
    }

    public static String adminLogin() {
        return getProperty("adminLogin");
    }

    public static String adminPassword() {
        return getProperty("adminPassword");
    }

    public static String apiBaseUrl() {
        return getProperty("server") + getProperty("apiVersion");
    }

    public static String uiBaseUrl() {
        return getProperty("baseUIUrl");
    }

    public static String remote() {
        return getProperty("remote");
    }

    public static String browser() {
        return getProperty("browser");
    }

    public static String browserSize() {
        return getProperty("browserSize");
    }

}
