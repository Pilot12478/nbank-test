package common.assertions;

import api.models.dto.CreateTransferFraudCheckModelResponse;
import api.models.dto.CreateTransferModelResponse;
import api.models.dto.FraudStatus;
import org.assertj.core.api.SoftAssertions;

import java.util.Comparator;

public class TransferFraudCheckAsserts {
    private static final String[] GENERATED_FIELDS = {
            "transactionId",
            "status",
            "message"
    };

    private final CreateTransferFraudCheckModelResponse response;
    private final SoftAssertions softly;
    public TransferFraudCheckAsserts(CreateTransferFraudCheckModelResponse response, SoftAssertions softly) {
        this.response = response;
        this.softly = softly;
    }

    public static TransferFraudCheckAsserts assertThatTransfer(SoftAssertions softly, CreateTransferFraudCheckModelResponse response) {
        return new TransferFraudCheckAsserts(response, softly);
    }
    public TransferFraudCheckAsserts hasStatus(FraudStatus status){
        softly.assertThat(response.getStatus()).isEqualTo(status);
        return this;
    }

    public TransferFraudCheckAsserts hasMessage(String message){
        softly.assertThat(response.getMessage()).isEqualTo(message);
        return this;
    }




    public  TransferFraudCheckAsserts assertMatches(
                                     CreateTransferFraudCheckModelResponse expected) {
        softly.assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields(GENERATED_FIELDS)
                .withComparatorForType(Comparator.comparingDouble(d -> Math.round(d * 100)), Double.class)
                .isEqualTo(expected);
        softly.assertThat(response.getTransactionId()).isPositive();
        return this;
    }


}
