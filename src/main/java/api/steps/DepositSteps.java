package api.steps;

import io.restassured.specification.ResponseSpecification;
import api.models.dto.DepositModelRequest;
import api.models.dto.DepositModelResponse;
import api.skelethon.Endpoint;
import api.skelethon.requesters.CrudRequester;
import api.skelethon.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.extensions.TestUser;

public class DepositSteps {
    public static DepositModelResponse depositAccount(TestUser user, double sum) {
        return depositAccount(user.credentials(), user.accountId(), sum);
    }

    public static String depositExpectingBadRequest(TestUser user, double sum) {
        return depositExpectingBadRequest(user.credentials(), user.accountId(), sum);
    }

    public static DepositModelResponse depositAccount(UserInfo userInfo, int accId, double sum) {
        return new ValidatedCrudRequester<DepositModelResponse>(RequestSpecs.userAuthReq(userInfo), ResponseSpecs.ok(), Endpoint.DEPOSIT)
                .post(DepositModelRequest
                        .builder()
                        .id(accId)
                        .balance(sum)
                        .build()

                );


    }
    private static String depositAccountWithSpec(UserInfo userInfo, int accId, double sum, ResponseSpecification responseSpecification) {
        return new CrudRequester(RequestSpecs.userAuthReq(userInfo), responseSpecification, Endpoint.DEPOSIT)
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
