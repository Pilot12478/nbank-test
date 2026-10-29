package api.steps;

import io.restassured.specification.ResponseSpecification;
import api.models.dto.CreateTransferModelRequest;
import api.models.dto.CreateTransferModelResponse;
import api.skelethon.Endpoint;
import api.skelethon.requesters.CrudRequester;
import api.skelethon.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.extensions.TestUser;

public class TransferSteps {
    public static CreateTransferModelResponse createTransfer(TestUser sender, int senderAccountId,
                                                             double sum, int receiverAccountId) {
        return createTransfer(sender.credentials(), senderAccountId, sum, receiverAccountId);
    }

    public static String transferExpectingBadRequest(TestUser sender, int senderAccountId,
                                                     double sum, int receiverAccountId) {
        return transferExpectingBadRequest(sender.credentials(), senderAccountId, sum, receiverAccountId);
    }

    public static CreateTransferModelResponse createTransfer(UserInfo userInfo, int senderAccountId,
                                                             double sum, int receiverAccountId) {
        return new ValidatedCrudRequester<CreateTransferModelResponse>(RequestSpecs.userAuthReq(userInfo), ResponseSpecs.ok(), Endpoint.TRANSFER)
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
        return new CrudRequester(RequestSpecs.userAuthReq(userInfo), responseSpecification, Endpoint.TRANSFER)
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
