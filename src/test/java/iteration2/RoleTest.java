package iteration2;

import Utils.AccountInfo;
import models.*;
import org.junit.jupiter.api.*;
import specs.ResponseSpecs;


import static Utils.HelperForIteration2.*;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class RoleTest extends BaseTest {
    private static final String VALID_USER_NAME = "John Duck";
    private static final String INVALID_USER_NAME = "John";
    private AccountInfo accountInfo;
    private static final String INVALID_NAME_MSG = "Name must contain two words with letters only";


    @BeforeEach
    public void preconditionForSuccessTest() {
        accountInfo = createUserAndAccount();
    }
    @AfterEach
    public void deleteUserAccount() {
        deleteUser(accountInfo);
    }
    @Test
    @DisplayName("Проверка успешной смены имени")
    public void successChangeNameTest() {
        UpdateUserNameModelResponse userModelResponse = updateUserName(accountInfo, VALID_USER_NAME, ResponseSpecs.ok())
                .extract().as(UpdateUserNameModelResponse.class);
        UserModelResponseProfile userProfileResponse = getUserAccount(accountInfo)
                .extract().as(UserModelResponseProfile.class);
        softly.assertThat(VALID_USER_NAME).isEqualTo(userModelResponse.getCustomer().getName());
        softly.assertThat(VALID_USER_NAME).isEqualTo(userProfileResponse.getName());

    }

    @Test
    @DisplayName("Проверка сценария с ошибкой при вводе имени одним словом")
    public void negativeChangeNameTest() {

        String actualMessage = updateUserName(accountInfo, INVALID_USER_NAME, ResponseSpecs.badRequest())
                .extract().asString();

        UserModelResponseProfile userProfileResponse = getUserAccount(accountInfo)
                .extract().as(UserModelResponseProfile.class);

        softly.assertThat(INVALID_NAME_MSG).isEqualTo(actualMessage);
        softly.assertThat(userProfileResponse.getName()).isNull();

        given()
                .header("Authorization", userAuthToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get(BASE_URL + "/api/v1/customer/profile")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("name", Matchers.nullValue());

    }
}
