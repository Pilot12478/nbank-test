package api.models.expected;

import lombok.Getter;

@Getter
public enum FraudFallback {
    SERVICE_ERROR("External fraud service returned unexpected response"),
    UNEXPECTED_ERROR("Unexpected error during fraud check: 500 Server Error: [no body]");
    final double RISK_SCORE = 0.5;
    public static final boolean REQUIRES_MANUAL_REVIEW = true;
    public static final boolean REQUIRES_VERIFICATION = false;
    private String message;

    FraudFallback(String message) {
        this.message = message;
    }

}
