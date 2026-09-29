package errors;

public final class DepositErrors {
    public static final String UNAUTHORIZED_ACCESS = "Unauthorized access to account";
    public static final String EXCEEDS_LIMIT = "Deposit amount cannot exceed 5000";
    public static final String MIN_AMOUNT = "Deposit amount must be at least 0.01";

    private DepositErrors() {}
}