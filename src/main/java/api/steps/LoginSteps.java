package api.steps;

import api.models.LoginUserModelRequest;
import api.skelethon.Endpoint;
import api.skelethon.requesters.CrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

public class LoginSteps {
    public static String login(UserInfo userInfo) {
        return new CrudRequester(RequestSpecs.adminAuthReq(), ResponseSpecs.ok(), Endpoint.LOGIN)
                .post(LoginUserModelRequest.builder()
                        .username(userInfo.getUsername())
                        .password(userInfo.getPassword())
                        .build()).extract().header("Authorization");
    }

}
