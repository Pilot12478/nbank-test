package db.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDao {
    private long id;
    private String username;
    private String password;
    private String name;
    private String role;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;



}
