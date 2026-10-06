package ui.pages;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum BankAlert {
    DEPOSITED_SUCCESSFULLY("✅ Successfully deposited $%s to account ACC%s!"),
    NAME_UPDATES_SUCCESSFULLY("✅ Name updated successfully!"),
    TRANSFERRED_SUCCESSFULLY("✅ Successfully transferred $%s to account ACC%s!"),
    INVALID_NAME("Name must contain two words with letters only"),
    INVALID_DEPOSIT_AMOUNT("❌ Please enter a valid amount."),
    OVER_LIMIT_DEPOSIT_AMOUNT("❌ Please deposit less or equal to 5000$."),
    OVER_LIMIT_TRANSFER_AMOUNT("❌ Error: Transfer amount cannot exceed 10000"),
    INVALID_TRANSFER_AMOUNT("❌ Error: Transfer amount must be at least 0.01"),
    TRANSFER_SUM_OVER_BALANCE("❌ Error: Invalid transfer: insufficient funds or invalid accounts"),
    ACCOUNT_NOT_EXIST("❌ No user found with this account number."),
    ACCOUNT_RECEIVER_INVALID("❌ Error: Invalid transfer: insufficient funds or invalid accounts");



    public final String message;

    BankAlert(String message) {
        this.message = message;
    }
    public String format(Object ...args){
        return String.format(message,args);
    }

    static void main() {
        System.out.println(BankAlert.INVALID_DEPOSIT_AMOUNT.getMessage());
    }
}
