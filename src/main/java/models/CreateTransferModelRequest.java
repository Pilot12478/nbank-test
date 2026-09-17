package models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateTransferModelRequest extends BaseModel{
    private int senderAccountId;
    private int receiverAccountId;
    private double amount;
}
