package iteration3.uiWithDB;

import api.models.dto.UserModelResponseProfile;
import api.steps.UserInfoSteps;
import common.annotations.ApiVersion;
import common.annotations.DataBase;
import common.annotations.UITest;
import common.annotations.UiSession;
import common.assertions.CommonSoftAssertions;
import common.extensions.TestUser;
import db.asserts.CustomerDBAsserts;
import iteration2.api.BaseTest;
import org.jooq.DSLContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.pages.BankAlert;
import ui.pages.UserProfilePage;

import static com.codeborne.selenide.Condition.text;
import static utils.Helper.generateInvalidName;
import static utils.Helper.generateName;
@UITest
@ApiVersion(versionApi = "with_database_with_fix")
public class UserNameUITestDB extends BaseTest {
    private static final String VALID_USER_NAME = generateName();
    private static final String INVALID_USER_NAME = generateInvalidName();
    private static final String DEFAULT_USER_NAME = "Noname";

    @Test
    @UiSession
    @DisplayName("Проверка успешного смены имени")
    public void shouldBeSuccessChangeNameTest(TestUser user,@DataBase DSLContext dslContext) {
        new UserProfilePage()
                .open()
                .changeName(VALID_USER_NAME)
                .checkAlertMessageAndAccept(BankAlert.NAME_UPDATES_SUCCESSFULLY)
                .refresh()
                .getName().shouldHave(text(VALID_USER_NAME));
        UserModelResponseProfile responseProfile = UserInfoSteps.getUserAccount(user.credentials());
        CommonSoftAssertions.assertThat(softly).isEqualTo(responseProfile.getName(), VALID_USER_NAME);
        CustomerDBAsserts.assertThatCustomer(softly,dslContext,responseProfile.getId()).hasName(VALID_USER_NAME);


    }

    @Test
    @UiSession
    @DisplayName("Проверка негативного сценария")
    public void shouldBeNegativeChangeNameTest(TestUser user,@DataBase DSLContext dslContext) {
        new UserProfilePage()
                .open()
                .changeName(INVALID_USER_NAME)
                .checkAlertMessageAndAccept(BankAlert.INVALID_NAME)
                .refresh()
                .getName().shouldHave(text(DEFAULT_USER_NAME));
        UserModelResponseProfile responseProfile = UserInfoSteps.getUserAccount(user.credentials());
        CommonSoftAssertions.assertThat(softly).isNull(responseProfile.getName());
        CustomerDBAsserts.assertThatCustomer(softly,dslContext,responseProfile.getId()).hasName(null);


    }
}
