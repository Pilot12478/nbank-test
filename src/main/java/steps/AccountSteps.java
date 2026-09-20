package steps;

import models.CreateAccountModelResponse;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.ValidatedCrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import static steps.UserInfoSteps.getUserAccount;


public class AccountSteps {
    public static int createAccount(UserInfo userInfo) {
        return new ValidatedCrudRequester<CreateAccountModelResponse>(RequestSpecs.userAuthReq(userInfo.getUsername(), userInfo.getPassword()),
                ResponseSpecs.created(), Endpoint.ACCOUNTS)
                .post(null).getId();


    }
    public static double getAccountBalance(UserInfo userInfo, int accountId) {
        return getUserAccount(userInfo)
                .getAccounts().stream()
                .filter(a -> a.getId() == accountId)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Account not found: " + accountId))
                .getBalance();
    }
}
