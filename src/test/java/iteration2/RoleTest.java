package iteration2;

import Utils.AccountInfo;
import models.*;
import org.junit.jupiter.api.*;
import specs.ResponseSpecs;


import static Utils.HelperForIteration2.*;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class RoleTest {
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
        assertEquals(VALID_USER_NAME, userModelResponse.getCustomer().getName());

        UserModelResponseProfile userProfileResponse = getUserAccount(accountInfo)
                .extract().as(UserModelResponseProfile.class);

        assertEquals(VALID_USER_NAME, userProfileResponse.getName());

    }

    @Test
    @DisplayName("Проверка сценария с ошибкой при вводе имени одним словом")
    public void negativeChangeNameTest() {

        String actualMessage = updateUserName(accountInfo, INVALID_USER_NAME, ResponseSpecs.badRequest())
                .extract().asString();

        assertEquals(INVALID_NAME_MSG, actualMessage);
        UserModelResponseProfile userProfileResponse = getUserAccount(accountInfo)
                .extract().as(UserModelResponseProfile.class);

        assertNull(userProfileResponse.getName());


    }
}
