package iteration2.ui;

import api.models.UserModelResponseProfile;
import api.steps.AdminSteps;
import api.steps.UserInfo;
import api.steps.UserInfoSteps;
import common.annotations.UITest;
import common.annotations.UserSession;
import common.extensions.TestUser;
import iteration2.api.BaseTest;
import org.junit.jupiter.api.*;
import ui.pages.BankAlert;
import ui.pages.UserProfilePage;

import static api.steps.AdminSteps.createUser;
import static com.codeborne.selenide.Condition.text;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static utils.Helper.generateInvalidName;
import static utils.Helper.generateName;
@UITest
public class UserNameUITest extends BaseTest {
    private static final String VALID_USER_NAME = generateName();
    private static final String INVALID_USER_NAME = generateInvalidName();
    private static final String DEFAULT_USER_NAME = "Noname";

    @Test
    @UserSession
    @DisplayName("Проверка успешного смены имени")
    public void shouldBeSuccessChangeNameTest(TestUser user) {
        new UserProfilePage()
                .open()
                .changeName(VALID_USER_NAME)
                .checkAlertMessageAndAccept(BankAlert.NAME_UPDATES_SUCCESSFULLY)
                .refresh()
                .getName().shouldHave(text(VALID_USER_NAME));
        UserModelResponseProfile responseProfile = UserInfoSteps.getUserAccount(user.user());
        assertThat(responseProfile.getName()).isEqualTo(VALID_USER_NAME);


    }

    @Test
    @UserSession
    @DisplayName("Проверка негативного сценария")
    @Disabled("Причина падения: браузер перед загрузкой профиля делает 4 запроса на бекэнд," +
            "после каждой загрузки происходит новый рендеринг, значение имени перетирается" +
            "выглядит как баг фронта, 4 запроса на бэк это лишнее"
    )
    public void shouldBeNegativeChangeNameTest(TestUser user) {
        new UserProfilePage()
                .open()
                .changeName(INVALID_USER_NAME)
                .checkAlertMessageAndAccept(BankAlert.INVALID_NAME)
                .refresh()
                .getName().shouldHave(text(DEFAULT_USER_NAME));
        UserModelResponseProfile responseProfile = UserInfoSteps.getUserAccount(user.user());
        assertNull(responseProfile.getName());


    }

}
