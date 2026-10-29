package api.steps;

import api.models.dto.BaseModel;
import api.models.dto.CreateTransferFraudCheckModelResponse;
import api.models.dto.CreateTransferModelRequest;
import api.skelethon.Endpoint;
import api.skelethon.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.extensions.TestUser;

public class TransferFraudCheckSteps {
    public static CreateTransferFraudCheckModelResponse createTransferWithFraudCheck(CreateTransferModelRequest request, UserInfo userInfo) {
        return new ValidatedCrudRequester<CreateTransferFraudCheckModelResponse>(
                RequestSpecs.userAuthReq(userInfo),
                ResponseSpecs.ok(),
                Endpoint.TRANSFER_FRAUD_CHECK)
                .post(request);
    }
}
