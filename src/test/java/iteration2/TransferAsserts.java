package iteration2;

import models.CreateTransferModelResponse;
import org.assertj.core.api.SoftAssertions;

import static errors.TransferErrors.TRANSFER_SUCCESS;
import static org.assertj.core.api.Assertions.offset;

public class TransferAsserts {
    private final CreateTransferModelResponse response;
    private final SoftAssertions softly;

    private TransferAsserts(CreateTransferModelResponse response, SoftAssertions softly) {
        this.response = response;
        this.softly = softly;
    }

    public static TransferAsserts assertThatTransfer(CreateTransferModelResponse response, SoftAssertions softly) {
        return new TransferAsserts(response, softly);
    }

    public TransferAsserts isSuccessful(double expectedSum,
                                        int expectedSenderId,
                                        int expectedReceiverId) {
        softly.assertThat(response.getAmount()).isCloseTo(expectedSum, offset(0.001));
        softly.assertThat(response.getSenderAccountId()).isEqualTo(expectedSenderId);
        softly.assertThat(response.getReceiverAccountId()).isEqualTo(expectedReceiverId);
        softly.assertThat(response.getMessage()).isEqualTo(TRANSFER_SUCCESS);
        return this;
    }
}
