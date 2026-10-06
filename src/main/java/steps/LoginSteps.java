package steps;

import io.restassured.response.ValidatableResponse;
import models.LoginUserModelRequest;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

public class LoginSteps {
    public static String login(UserInfo userInfo) {
        return new CrudRequester(RequestSpecs.adminAuthReq(), ResponseSpecs.ok(), Endpoint.LOGIN)
                .post(LoginUserModelRequest.builder()
                        .username(userInfo.getUsername())
                        .password(userInfo.getPassword())
                        .build()).extract().header("Authorization");
    }

}
