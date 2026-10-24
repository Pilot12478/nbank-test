package iteration2.api;

import api.models.dto.UpdateUserNameModelResponse;
import api.models.dto.UserModelResponseProfile;
import api.steps.UserNameSteps;
import common.annotations.ApiVersion;
import common.annotations.UserSession;
import common.assertions.CommonSoftAssertions;
import common.extensions.TestUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static api.errors.UserNameErrors.INVALID_NAME;
import static api.steps.UserInfoSteps.getUserAccount;
import static api.steps.UserNameSteps.updateUserName;
import static utils.Helper.generateInvalidName;
import static utils.Helper.generateName;

public class UserNameTest extends BaseTest {
    private static final String VALID_USER_NAME = generateName();
    private static final String INVALID_USER_NAME = generateInvalidName();

    @Test
    @UserSession
    @DisplayName("Проверка успешной смены имени")
    @ApiVersion(versionApi = "self-transfer-fix")
    public void successChangeNameTest(TestUser user) {
        UpdateUserNameModelResponse userModelResponse = updateUserName(user.credentials(), VALID_USER_NAME);
        UserModelResponseProfile userProfileResponse = getUserAccount(user.credentials());
        CommonSoftAssertions.assertThat(softly)
                .isEqualTo(userModelResponse.getCustomer().getName(), VALID_USER_NAME)
                .isEqualTo(userProfileResponse.getName(), VALID_USER_NAME);

    }

    @Test
    @UserSession
    @DisplayName("Проверка сценария с ошибкой при вводе имени одним словом")
    @ApiVersion(versionApi = "self-transfer-fix")
    public void negativeChangeNameTest(TestUser user) {
        String actualMessage = UserNameSteps.updateUserNameExpectingBadRequest(user.credentials(), INVALID_USER_NAME);
        UserModelResponseProfile userProfileResponse = getUserAccount(user.credentials());
        CommonSoftAssertions.assertThat(softly)
                .isEqualTo(actualMessage,INVALID_NAME)
                .isNull(userProfileResponse.getName());

    }
}
