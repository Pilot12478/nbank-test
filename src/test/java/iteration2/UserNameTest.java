package iteration2;

import models.UpdateUserNameModelResponse;
import models.UserModelResponseProfile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import steps.AdminSteps;
import steps.UserInfo;
import steps.UserNameSteps;

import static steps.UserInfoSteps.getUserAccount;
import static steps.UserNameSteps.INVALID_NAME_MSG;
import static steps.UserNameSteps.updateUserName;
import static utils.Helper.generateName;

public class UserNameTest extends BaseTest {
    private static final String VALID_USER_NAME = generateName();
    private static final String INVALID_USER_NAME = "John";
    private UserInfo userInfo;


    @BeforeEach
    public void setUp() {
        userInfo = AdminSteps.createUser();
    }

    @AfterEach
    public void deleteUserAccount() {
        AdminSteps.deleteUser(userInfo);
    }

    @Test
    @DisplayName("Проверка успешной смены имени")
    public void successChangeNameTest() {
        UpdateUserNameModelResponse userModelResponse = updateUserName(userInfo, VALID_USER_NAME);
        UserModelResponseProfile userProfileResponse = getUserAccount(userInfo);
        softly.assertThat(userModelResponse.getCustomer().getName()).isEqualTo(VALID_USER_NAME);
        softly.assertThat(userProfileResponse.getName()).isEqualTo(VALID_USER_NAME);

    }

    @Test
    @DisplayName("Проверка сценария с ошибкой при вводе имени одним словом")
    public void negativeChangeNameTest() {
        String actualMessage = UserNameSteps.updateUserNameExpectingBadRequest(userInfo, INVALID_USER_NAME);
        UserModelResponseProfile userProfileResponse = getUserAccount(userInfo);
        softly.assertThat(actualMessage).isEqualTo(INVALID_NAME_MSG);
        softly.assertThat(userProfileResponse.getName()).isNull();

    }
}
