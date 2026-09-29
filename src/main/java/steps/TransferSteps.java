package steps;

import io.restassured.specification.ResponseSpecification;
import models.CreateTransferModelRequest;
import models.CreateTransferModelResponse;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import requests.skelethon.requesters.ValidatedCrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

public class TransferSteps {
    public static CreateTransferModelResponse createTransfer(UserInfo userInfo, int senderAccountId,
                                                             double sum, int receiverAccountId) {
        return new ValidatedCrudRequester<CreateTransferModelResponse>(RequestSpecs.userAuthReq(userInfo.getUsername(),
                userInfo.getPassword()), ResponseSpecs.ok(), Endpoint.TRANSFER)
                .post(CreateTransferModelRequest
                        .builder()
                        .amount(sum)
                        .senderAccountId(senderAccountId)
                        .receiverAccountId(receiverAccountId)
                        .build());
    }

    private static String createTransferWithSpec(UserInfo userInfo, int senderAccountId,
                                                double sum, int receiverAccountId,
                                                ResponseSpecification responseSpecification) {
        return new CrudRequester(RequestSpecs.userAuthReq(userInfo.getUsername(),
                userInfo.getPassword()), responseSpecification, Endpoint.TRANSFER)
                .post(CreateTransferModelRequest
                        .builder()
                        .amount(sum)
                        .senderAccountId(senderAccountId)
                        .receiverAccountId(receiverAccountId)
                        .build()).extract().asString();
    }

    public static String transferExpectingBadRequest(UserInfo userInfo, int senderAccountId,
                                                     double sum, int receiverAccountId) {
        return createTransferWithSpec(userInfo, senderAccountId, sum, receiverAccountId, ResponseSpecs.badRequest());

    }

}
