package Utils;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;

import java.util.List;

import static io.restassured.RestAssured.given;

public class HelperForIteration2 {
    public static final String BASE_URL = "http://localhost:4111";
    public static final String ADMIN_TOKEN = "Basic YWRtaW46YWRtaW4=";

    public static String createUser(String userName, String password, String role) {
        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", ADMIN_TOKEN)
                .body(String.format(

                        """
                                {
                                        "username": "%s",
                                        "password": "%s",
                                        "role": "%s"
                                      }""", userName, password, role
                ))
                .post(BASE_URL + "/api/v1/admin/users")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .extract()
                .header("Authorization");
    }

    public static int createAccount(String authToken) {
        return given()
                .header("Authorization", authToken).
                post(BASE_URL+"/api/v1/accounts")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .extract()
                .path("id");

    }
    public static void depositAccount(String authToken, int accountId, int sum){
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", authToken)
                .body(String.format("""
                        {
                          "id": %d,
                          "balance": %s
                        }
                        """, accountId, sum))
                .post(BASE_URL + "/api/v1/accounts/deposit");

    }

    public static void logConfig() {
        RestAssured.filters(
                List.of(new RequestLoggingFilter(),
                        new ResponseLoggingFilter())
        );

    }
}
