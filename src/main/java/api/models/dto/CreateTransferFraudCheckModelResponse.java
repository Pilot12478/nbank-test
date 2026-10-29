package api.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTransferFraudCheckModelResponse extends BaseModel{
    private FraudStatus status;
    private double amount;
    private String message;
    private long transactionId;
    private double fraudRiskScore;
    private String fraudReason;
    private boolean requiresVerification;
    private boolean requiresManualReview;
    private long receiverAccountId;
    private long senderAccountId;

}
