package api.steps;

import api.models.UserModelResponseProfile;
import api.skelethon.Endpoint;
import api.skelethon.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

public class UserInfoSteps {
    public static UserModelResponseProfile getUserAccount(UserInfo userInfo) {
        return new ValidatedCrudRequester<UserModelResponseProfile>(
                RequestSpecs.userAuthReq(userInfo), ResponseSpecs.ok(), Endpoint.USER_PROFILE)
                .get();
    }
}
