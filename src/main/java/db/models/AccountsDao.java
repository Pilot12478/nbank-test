package db.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountsDao {
    private long id;
    private String account_number;
    private BigDecimal balance;
    private long customer_id;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;

}
