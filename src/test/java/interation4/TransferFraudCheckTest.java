package interation4;

import api.models.dto.CreateTransferFraudCheckModelResponse;
import api.models.dto.CreateTransferModelRequest;
import api.models.dto.FraudStatus;
import api.models.expected.FraudFallback;
import api.models.requests.TransferRequests;
import common.annotations.FraudCheckMock;
import common.annotations.UserSession;
import common.extensions.TestUser;
import iteration2.api.BaseTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static api.models.expected.ExpectedResponses.fraudTransferWithNegativeAnswer;
import static api.models.expected.ExpectedResponses.fraudTransferWithSuccessAnswer;
import static api.steps.TransferFraudCheckSteps.createTransferWithFraudCheck;
import static common.annotations.FraudCheckMock.Fault.CONNECTION_RESET;
import static common.annotations.FraudCheckMock.Fault.HTTP_ERROR;
import static common.assertions.TransferFraudCheckAsserts.assertThatTransfer;
import static utils.constants.BankLimits.MAX_DEPOSIT_SUM;

public class TransferFraudCheckTest extends BaseTest {
    private static final double STANDARD_TRANSFER_SUM = 9999.99;
    private static final double INITIAL_BALANCE = MAX_DEPOSIT_SUM * 2;

    private record TransferExecution(
            CreateTransferModelRequest request,
            CreateTransferFraudCheckModelResponse response
    ) {
        private static TransferExecution executeTransfer(TestUser sender, double amount) {
            CreateTransferModelRequest request = TransferRequests.createTransferBody(
                    amount,
                    sender.accountId(),
                    sender.accountId(1)
            );

            CreateTransferFraudCheckModelResponse response =
                    createTransferWithFraudCheck(request, sender.credentials());

            return new TransferExecution(request, response);
        }
    }

    @UserSession(accounts = 2, balance = INITIAL_BALANCE)
    @DisplayName("Проверка успешного перевода при  decision=APPROVED от FRAUD сервиса")
    @FraudCheckMock()
    @Test
    public void checkTransferApproveImmediately(TestUser sender, FraudCheckMock fraudCheckMock) {
        TransferExecution execution = TransferExecution.executeTransfer(sender, STANDARD_TRANSFER_SUM);
        assertThatTransfer(softly, execution.response)
                .assertMatches(fraudTransferWithSuccessAnswer(execution.request, fraudCheckMock))
                .hasStatus(FraudStatus.APPROVED)
                .hasMessage(FraudStatus.APPROVED.getMessage());

    }

    @UserSession(accounts = 2, balance = INITIAL_BALANCE)
    @DisplayName("Проверка блокировки перевода при  decision=BLOCKED от FRAUD сервиса")
    @FraudCheckMock(decision = "BLOCKED")
    @Test
    public void checkTransferBlocked(TestUser sender, FraudCheckMock fraudCheckMock) {
        TransferExecution execution = TransferExecution.executeTransfer(sender, STANDARD_TRANSFER_SUM);
        assertThatTransfer(softly, execution.response)
                .assertMatches(fraudTransferWithSuccessAnswer(execution.request, fraudCheckMock))
                .hasStatus(FraudStatus.BLOCKED)
                .hasMessage(FraudStatus.BLOCKED.getMessage());

    }

    @UserSession(accounts = 2, balance = INITIAL_BALANCE)
    @DisplayName("Проверка отправки перевода на ручную проверку при decision=REVIEW_REQUIRED от FRAUD сервиса")
    @FraudCheckMock(decision = "REVIEW_REQUIRED")
    @Test
    public void checkTransferReviewRequiredScenarioWithDecisionReviewRequired(TestUser sender, FraudCheckMock fraudCheckMock) {
        TransferExecution execution = TransferExecution.executeTransfer(sender, STANDARD_TRANSFER_SUM);
        assertThatTransfer(softly, execution.response)
                .assertMatches(fraudTransferWithSuccessAnswer(execution.request, fraudCheckMock))
                .hasStatus(FraudStatus.MANUAL_REVIEW_REQUIRED)
                .hasMessage(FraudStatus.MANUAL_REVIEW_REQUIRED.getMessage());

    }

    @UserSession(accounts = 2, balance = INITIAL_BALANCE)
    @DisplayName("Проверка отправки перевода на ручную проверку при requiresManualReview=true от FRAUD сервиса")
    @FraudCheckMock(requiresManualReview = true)
    @Test
    public void checkTransferReviewRequiredScenarioWithFlagRequiresManualReview(TestUser sender, FraudCheckMock fraudCheckMock) {
        TransferExecution execution = TransferExecution.executeTransfer(sender, STANDARD_TRANSFER_SUM);
        assertThatTransfer(softly, execution.response)
                .assertMatches(fraudTransferWithSuccessAnswer(execution.request, fraudCheckMock))
                .hasStatus(FraudStatus.MANUAL_REVIEW_REQUIRED)
                .hasMessage(FraudStatus.MANUAL_REVIEW_REQUIRED.getMessage());

    }

    @UserSession(accounts = 2, balance = INITIAL_BALANCE)
    @DisplayName("Проверка отправки перевода на ручную проверку при requiresManualReview=true от FRAUD сервиса")
    @FraudCheckMock(decision = "VERIFICATION_REQUIRED")
    @Test
    public void checkTransferVerificationRequiredScenarioWithDecisionVerificationRequired(TestUser sender, FraudCheckMock fraudCheckMock) {
        TransferExecution execution = TransferExecution.executeTransfer(sender, STANDARD_TRANSFER_SUM);
        assertThatTransfer(softly, execution.response)
                .assertMatches(fraudTransferWithSuccessAnswer(execution.request, fraudCheckMock))
                .hasStatus(FraudStatus.VERIFICATION_REQUIRED)
                .hasMessage(FraudStatus.VERIFICATION_REQUIRED.getMessage());

    }

    @UserSession(accounts = 2, balance = INITIAL_BALANCE)
    @DisplayName("Проверка отправки перевода на ручную проверку при requiresManualReview=true от FRAUD сервиса")
    @FraudCheckMock(additionalVerificationRequired = true)
    @Test
    @Disabled("Баг на стороне сервиса отправки перевода")
    public void checkTransferVerificationRequiredScenarioWithFlagAdditionalVerificationRequired(TestUser sender, FraudCheckMock fraudCheckMock) {
        TransferExecution execution = TransferExecution.executeTransfer(sender, STANDARD_TRANSFER_SUM);
        assertThatTransfer(softly, execution.response)
                .assertMatches(fraudTransferWithSuccessAnswer(execution.request, fraudCheckMock))
                .hasStatus(FraudStatus.VERIFICATION_REQUIRED)
                .hasMessage(FraudStatus.VERIFICATION_REQUIRED.getMessage());

    }

    @UserSession(accounts = 2, balance = INITIAL_BALANCE)
    @DisplayName("Проверка сценария от FRAUD сервиса получен статус код не 200")
    @FraudCheckMock(
            fault = HTTP_ERROR,
            statusCode = 500)
    @Test
    public void checkTransferWithStatusCodeNotEqual200FromFraudService(TestUser sender) {
        TransferExecution execution = TransferExecution.executeTransfer(sender, STANDARD_TRANSFER_SUM);
        assertThatTransfer(softly, execution.response)
                .assertMatches(fraudTransferWithNegativeAnswer(execution.request, FraudFallback.UNEXPECTED_ERROR.getMessage()))
                .hasStatus(FraudStatus.MANUAL_REVIEW_REQUIRED)
                .hasMessage(FraudStatus.MANUAL_REVIEW_REQUIRED.getMessage());

    }

    @UserSession(accounts = 2, balance = INITIAL_BALANCE)
    @DisplayName("Проверка сценария от FRAUD сервиса получен таймаут")
    @FraudCheckMock(
            fault = CONNECTION_RESET,
            delayMs = 5000)
    @Test
    public void checkTransferWithTimeoutFromFraudService(TestUser sender) {
        TransferExecution execution = TransferExecution.executeTransfer(sender, STANDARD_TRANSFER_SUM);
        assertThatTransfer(softly, execution.response)
                .assertMatches(fraudTransferWithNegativeAnswer(execution.request, FraudFallback.SERVICE_ERROR.getMessage()))
                .hasStatus(FraudStatus.MANUAL_REVIEW_REQUIRED)
                .hasMessage(FraudStatus.MANUAL_REVIEW_REQUIRED.getMessage());

    }

}
