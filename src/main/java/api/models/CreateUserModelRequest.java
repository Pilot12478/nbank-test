package api.models;

import api.generators.GeneratorRule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class CreateUserModelRequest extends BaseModel{
    @GeneratorRule(regex = "^[A-Za-z0-9._-]{3,15}$")
    private String username;
    @GeneratorRule(regex = "[a-z]{3}[A-Z]{3}[0-9]{3}[@#%&!]{2}")
    private String password;
    private String role;
}
