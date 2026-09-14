package iteration2;

import io.restassured.http.ContentType;
import models.*;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import requests.UpdateUserNameRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;


import static Utils.HelperForIteration2.*;
import static Utils.TestDataGenerator.generateUserName;
import static Utils.TestDataGenerator.getDefaultPassword;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.responseSpecification;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class RoleTest {
    private static final String VALID_USER_NAME = "John Duck";
    private static final String INVALID_USER_NAME = "John";
    private AccountInfo accountInfo;


    @BeforeEach
    public void preconditionForSuccessTest() {
        accountInfo = createUserAndAccount();
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

        assertEquals("Name must contain two words with letters only", actualMessage);
        UserModelResponseProfile userProfileResponse = getUserAccount(accountInfo)
                .extract().as(UserModelResponseProfile.class);

        assertNull(userProfileResponse.getName());


    }
}
