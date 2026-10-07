package iteration2.api;

import api.models.UpdateUserNameModelResponse;
import api.models.UserModelResponseProfile;
import api.steps.UserNameSteps;
import common.annotations.UITest;
import common.annotations.UserSession;
import common.extensions.TestUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static api.errors.UserNameErrors.INVALID_NAME;
import static api.steps.UserInfoSteps.getUserAccount;
import static api.steps.UserNameSteps.updateUserName;
import static utils.Helper.generateInvalidName;
import static utils.Helper.generateName;
@UITest
public class UserNameTest extends BaseTest {
    private static final String VALID_USER_NAME = generateName();
    private static final String INVALID_USER_NAME = generateInvalidName();

    @Test
    @UserSession
    @DisplayName("Проверка успешной смены имени")
    public void successChangeNameTest(TestUser user) {
        UpdateUserNameModelResponse userModelResponse = updateUserName(user.user(), VALID_USER_NAME);
        UserModelResponseProfile userProfileResponse = getUserAccount(user.user());
        softly.assertThat(userModelResponse.getCustomer().getName()).isEqualTo(VALID_USER_NAME);
        softly.assertThat(userProfileResponse.getName()).isEqualTo(VALID_USER_NAME);

    }

    @Test
    @UserSession
    @DisplayName("Проверка сценария с ошибкой при вводе имени одним словом")
    public void negativeChangeNameTest(TestUser user) {
        String actualMessage = UserNameSteps.updateUserNameExpectingBadRequest(user.user(), INVALID_USER_NAME);
        UserModelResponseProfile userProfileResponse = getUserAccount(user.user());
        softly.assertThat(actualMessage).isEqualTo(INVALID_NAME);
        softly.assertThat(userProfileResponse.getName()).isNull();

    }
}
