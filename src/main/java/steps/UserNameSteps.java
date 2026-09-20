package steps;

import io.restassured.specification.ResponseSpecification;
import models.UpdateUserNameModelRequest;
import models.UpdateUserNameModelResponse;
import models.UserModelResponseProfile;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import requests.skelethon.requesters.ValidatedCrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

public class UserNameSteps {
    public static UpdateUserNameModelResponse updateUserName(UserInfo userInfo, String name) {
        return new ValidatedCrudRequester<UpdateUserNameModelResponse>(RequestSpecs.userAuthReq(
                userInfo.getUsername(), userInfo.getPassword()), ResponseSpecs.ok(), Endpoint.USER_NAME)
                .update(UpdateUserNameModelRequest
                        .builder()
                        .name(name)
                        .build());
    }
    private static String updateUserNameWithSpec(UserInfo userInfo, String name, ResponseSpecification responseSpecification) {
        return new CrudRequester(RequestSpecs.userAuthReq(
                userInfo.getUsername(), userInfo.getPassword()), responseSpecification, Endpoint.USER_NAME)
                .update(UpdateUserNameModelRequest
                        .builder()
                        .name(name)
                        .build()).extract().asString();
    }
    public static String updateUserNameExpectingBadRequest(UserInfo userInfo, String name){
        return  updateUserNameWithSpec(userInfo,name,ResponseSpecs.badRequest());

    }

}
