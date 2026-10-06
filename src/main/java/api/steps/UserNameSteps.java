package api.steps;

import io.restassured.specification.ResponseSpecification;
import api.models.UpdateUserNameModelRequest;
import api.models.UpdateUserNameModelResponse;
import api.skelethon.Endpoint;
import api.skelethon.requesters.CrudRequester;
import api.skelethon.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

public class UserNameSteps {
    public static UpdateUserNameModelResponse updateUserName(UserInfo userInfo, String name) {
        return new ValidatedCrudRequester<UpdateUserNameModelResponse>(RequestSpecs.userAuthReq(
               userInfo), ResponseSpecs.ok(), Endpoint.USER_NAME)
                .update(UpdateUserNameModelRequest
                        .builder()
                        .name(name)
                        .build());
    }
    private static String updateUserNameWithSpec(UserInfo userInfo, String name, ResponseSpecification responseSpecification) {
        return new CrudRequester(RequestSpecs.userAuthReq(
                userInfo), responseSpecification, Endpoint.USER_NAME)
                .update(UpdateUserNameModelRequest
                        .builder()
                        .name(name)
                        .build()).extract().asString();
    }
    public static String updateUserNameExpectingBadRequest(UserInfo userInfo, String name){
        return  updateUserNameWithSpec(userInfo,name,ResponseSpecs.badRequest());
    }
}
