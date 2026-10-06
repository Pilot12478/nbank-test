package api.errors;

public final class TransferErrors {
    public static final String MIN_AMOUNT = "Transfer amount must be at least 0.01";
    public static final String EXCEEDS_LIMIT = "Transfer amount cannot exceed 10000";
    public static final String INVALID_TRANSFER = "Invalid transfer: insufficient funds or invalid accounts";
    public static final String TRANSFER_SUCCESS = "Transfer successful";

    private TransferErrors() {}
}
