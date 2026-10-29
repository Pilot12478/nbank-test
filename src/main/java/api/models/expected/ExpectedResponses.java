package api.models.expected;

import api.models.dto.CreateTransferFraudCheckModelResponse;
import api.models.dto.CreateTransferModelRequest;
import common.annotations.FraudCheckMock;

public class ExpectedResponses {
    public static CreateTransferFraudCheckModelResponse fraudTransferWithSuccessAnswer(CreateTransferModelRequest request,
                                                                      FraudCheckMock mock) {
        CreateTransferFraudCheckModelResponse expected = new CreateTransferFraudCheckModelResponse();
        expected.setSenderAccountId(request.getSenderAccountId());
        expected.setReceiverAccountId(request.getReceiverAccountId());
        expected.setAmount(request.getAmount());
        expected.setFraudRiskScore(mock.riskScore());
        expected.setFraudReason(mock.reason());
        expected.setRequiresManualReview(mock.requiresManualReview());
        expected.setRequiresVerification(mock.additionalVerificationRequired());
        return expected;
    }

    public static CreateTransferFraudCheckModelResponse fraudTransferWithNegativeAnswer(CreateTransferModelRequest request,String reason) {
        CreateTransferFraudCheckModelResponse expected = new CreateTransferFraudCheckModelResponse();
        expected.setSenderAccountId(request.getSenderAccountId());
        expected.setReceiverAccountId(request.getReceiverAccountId());
        expected.setAmount(request.getAmount());
        expected.setFraudRiskScore(FraudFallback.SERVICE_ERROR.RISK_SCORE);
        expected.setFraudReason(reason);
        expected.setRequiresManualReview(FraudFallback.REQUIRES_MANUAL_REVIEW);
        expected.setRequiresVerification(FraudFallback.REQUIRES_VERIFICATION);
        return expected;
    }
}
