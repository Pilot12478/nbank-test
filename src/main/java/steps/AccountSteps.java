package steps;

import models.CreateAccountModelResponse;
import models.UserModelResponseProfile;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.ValidatedCrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.HashMap;
import java.util.Map;

import static steps.UserInfoSteps.getUserAccount;


public class AccountSteps {
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
