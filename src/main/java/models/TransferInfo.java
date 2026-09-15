package models;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferInfo {
    private String username;
    private String password;
    private int senderAccountId;
    private int receiverAccountId;
    private double sum;

}
