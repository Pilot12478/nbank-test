package iteration2;

import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import static Utils.HelperForIteration2.*;
import static Utils.TestDataGenerator.generateUserName;
import static Utils.TestDataGenerator.getDefaultPassword;
import static io.restassured.RestAssured.given;

public class RoleTest {
    private static String userAuthToken;
    private static final String VALID_USER_NAME = "John Duck";
    private static final String INVALID_USER_NAME = "John";

    @BeforeAll
    public static void setUp() {
        logConfig();

    }

    @BeforeEach
    public void preconditionForSuccessTest() {
        String username = generateUserName();
        String password = getDefaultPassword();
        String role = "USER";
        userAuthToken = createUser(username, password, role);

    }

    @Test
    @DisplayName("Проверка успешной смены имени")
    public void successChangeNameTest() {
        given()
                .header("Authorization", userAuthToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format("""
                        {
                          "name": "%s"
                        }
                        """, VALID_USER_NAME))
                .put(BASE_URL + "/api/v1/customer/profile")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("customer.name", Matchers.equalTo(VALID_USER_NAME));

        given()
                .header("Authorization", userAuthToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get(BASE_URL + "/api/v1/customer/profile")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("name", Matchers.equalTo(VALID_USER_NAME));
    }

    @Test
    @DisplayName("Проверка сценария с ошибкой при вводе имени одним словом")
    public void negativeChangeNameTest() {
        given()
                .header("Authorization", userAuthToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format("""
                        {
                          "name": "%s"
                        }
                        """, INVALID_USER_NAME))
                .put(BASE_URL + "/api/v1/customer/profile")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString("Name must contain two words with letters only"));

    }
}
