package models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepositModelResponse extends BaseModel{
    private long id;
    private String accountNumber;
    private double balance;
    private List<Transactions>transactions;
}
