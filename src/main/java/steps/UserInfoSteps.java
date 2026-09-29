package steps;

import models.UserModelResponseProfile;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.ValidatedCrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

public class UserInfoSteps {
    public static UserModelResponseProfile getUserAccount(UserInfo userInfo) {
        return new ValidatedCrudRequester<UserModelResponseProfile>(
                RequestSpecs.userAuthReq(userInfo.getUsername(), userInfo.getPassword()), ResponseSpecs.ok(), Endpoint.USER_PROFILE)
                .get();
    }
}
