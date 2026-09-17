package iteration2;

import Utils.AccountInfo;
import models.UpdateUserNameModelResponse;
import models.UserModelResponseProfile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import specs.ResponseSpecs;

import static Utils.HelperForIteration2.*;

public class UserNameTest extends BaseTest {
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
        UpdateUserNameModelResponse userModelResponse = updateUserNamePositive(accountInfo, VALID_USER_NAME, ResponseSpecs.ok());
        UserModelResponseProfile userProfileResponse = getUserAccount(accountInfo);
        softly.assertThat(VALID_USER_NAME).isEqualTo(userModelResponse.getCustomer().getName());
        softly.assertThat(VALID_USER_NAME).isEqualTo(userProfileResponse.getName());

    }

    @Test
    @DisplayName("Проверка сценария с ошибкой при вводе имени одним словом")
    public void negativeChangeNameTest() {

        String actualMessage = updateUserNameNegative(accountInfo, INVALID_USER_NAME, ResponseSpecs.badRequest())
                .extract().asString();

        UserModelResponseProfile userProfileResponse = getUserAccount(accountInfo);

        softly.assertThat(INVALID_NAME_MSG).isEqualTo(actualMessage);
        softly.assertThat(userProfileResponse.getName()).isNull();

    }
}
