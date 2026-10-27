package api.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"timestampAsString","amountAsDouble"})
public class Transactions {
    private Long id;
    private Double amount;
    private String type;
    private String timestamp;
    private Long relatedAccountId;
}
