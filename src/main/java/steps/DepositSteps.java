package steps;

import io.restassured.specification.ResponseSpecification;
import models.DepositModelRequest;
import models.DepositModelResponse;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import requests.skelethon.requesters.ValidatedCrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

public class DepositSteps {
    public static DepositModelResponse depositAccount(UserInfo userInfo, int accId, double sum) {
        return new ValidatedCrudRequester<DepositModelResponse>(RequestSpecs.userAuthReq(userInfo.getUsername(), userInfo.getPassword()), ResponseSpecs.ok(), Endpoint.DEPOSIT)
                .post(DepositModelRequest
                        .builder()
                        .id(accId)
                        .balance(sum)
                        .build()

                );


    }
    private static String depositAccountWithSpec(UserInfo userInfo, int accId, double sum, ResponseSpecification responseSpecification) {
        return new CrudRequester(RequestSpecs.userAuthReq(userInfo.getUsername(), userInfo.getPassword()), responseSpecification, Endpoint.DEPOSIT)
                .post(DepositModelRequest
                        .builder()
                        .id(accId)
                        .balance(sum)
                        .build()

                ).extract().asString();

    }
    public static String depositExpectingBadRequest(UserInfo userInfo, int accId, double sum){
        return depositAccountWithSpec(userInfo, accId,sum,ResponseSpecs.badRequest());
    }
    public static String depositExpectingForbidden(UserInfo userInfo, int accId, double sum){
        return depositAccountWithSpec(userInfo, accId,sum,ResponseSpecs.forbidden());
    }
}
