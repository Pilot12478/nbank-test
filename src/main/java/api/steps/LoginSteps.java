package api.steps;

import api.models.LoginUserModelRequest;
import api.skelethon.Endpoint;
import api.skelethon.requesters.CrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import static api.specs.Headers.AUTHORIZATION;

public class LoginSteps {
    public static String login(UserInfo userInfo) {
        return login(userInfo.getUsername(), userInfo.getPassword());
    }

    public static String login(String username, String password) {
        return new CrudRequester(RequestSpecs.unAuthReq(), ResponseSpecs.ok(), Endpoint.LOGIN)
                .post(LoginUserModelRequest.builder()
                        .username(username)
                        .password(password)
                        .build()).extract().header(AUTHORIZATION);
    }

}
