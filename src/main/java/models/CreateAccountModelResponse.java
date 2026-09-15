package models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateAccountModelResponse {
    private int id;
    private String accountNumber;
    private double balance;
    private List<String>transactions;
}
