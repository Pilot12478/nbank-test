package api.steps;

import api.models.dto.CreateAccountModelResponse;
import api.skelethon.Endpoint;
import api.skelethon.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.extensions.TestUser;

import static api.steps.UserInfoSteps.getUserAccount;


public class AccountSteps {
    public static int createAccount(TestUser user) {
        return createAccount(user.credentials());
    }

    public static double getAccountBalance(TestUser user) {
        return getAccountBalance(user.credentials(), user.accountId());
    }

    public static double getAccountBalance(TestUser user, int accountId) {
        return getAccountBalance(user.credentials(), accountId);
    }

    public static int createAccount(UserInfo userInfo) {
        return new ValidatedCrudRequester<CreateAccountModelResponse>(RequestSpecs.userAuthReq(userInfo),
                ResponseSpecs.created(), Endpoint.ACCOUNTS)
                .post(null).getId();


    }
    public static double getAccountBalance(UserInfo userInfo, int accountId) {
        return getUserAccount(userInfo)
                .getAccounts().stream()
                .filter(a -> a.getId() == accountId)
                .findFirst()
                .get()
                .getBalance();
    }


}
