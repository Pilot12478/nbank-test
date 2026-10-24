package iteration2.ui;

import api.models.dto.UserModelResponseProfile;
import api.steps.UserInfoSteps;
import common.annotations.ApiVersion;
import common.annotations.UITest;
import common.annotations.UiSession;
import common.assertions.CommonSoftAssertions;
import common.extensions.TestUser;
import iteration2.api.BaseTest;
import org.junit.jupiter.api.*;
import ui.pages.BankAlert;
import ui.pages.UserProfilePage;

import static com.codeborne.selenide.Condition.text;
import static utils.Helper.generateInvalidName;
import static utils.Helper.generateName;
@UITest
public class UserNameUITest extends BaseTest {
    private static final String VALID_USER_NAME = generateName();
    private static final String INVALID_USER_NAME = generateInvalidName();
    private static final String DEFAULT_USER_NAME = "Noname";

    @Test
    @UiSession
    @DisplayName("Проверка успешного смены имени")
    @ApiVersion(versionApi = "self-transfer-fix")
    public void shouldBeSuccessChangeNameTest(TestUser user) {
        new UserProfilePage()
                .open()
                .changeName(VALID_USER_NAME)
                .checkAlertMessageAndAccept(BankAlert.NAME_UPDATES_SUCCESSFULLY)
                .refresh()
                .getName().shouldHave(text(VALID_USER_NAME));
        UserModelResponseProfile responseProfile = UserInfoSteps.getUserAccount(user.credentials());
        CommonSoftAssertions.assertThat(softly).isEqualTo(responseProfile.getName(), VALID_USER_NAME);


    }

    @Test
    @UiSession
    @DisplayName("Проверка негативного сценария")
    @ApiVersion(versionApi = "self-transfer-fix")
    public void shouldBeNegativeChangeNameTest(TestUser user) {
        new UserProfilePage()
                .open()
                .changeName(INVALID_USER_NAME)
                .checkAlertMessageAndAccept(BankAlert.INVALID_NAME)
                .refresh()
                .getName().shouldHave(text(DEFAULT_USER_NAME));
        UserModelResponseProfile responseProfile = UserInfoSteps.getUserAccount(user.credentials());
        CommonSoftAssertions.assertThat(softly).isNull(responseProfile.getName());


    }

}
