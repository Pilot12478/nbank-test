package api.models.dto;

public enum FraudStatus {
    APPROVED("Transfer approved and processed immediately"),
    BLOCKED("Transfer blocked due to fraud detection"),
    MANUAL_REVIEW_REQUIRED("Transfer requires manual review"),
    VERIFICATION_REQUIRED("Additional verification required");

    private final String message;

    FraudStatus(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }


}
