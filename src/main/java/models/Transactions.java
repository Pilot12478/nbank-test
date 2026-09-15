package models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transactions {
    private Long id;
    private Double amount;
    private String type;
    private String timestamp;
    private Long relatedAccountId;
}
