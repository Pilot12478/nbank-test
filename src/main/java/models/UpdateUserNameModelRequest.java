package models;

import generators.GeneratorRule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserNameModelRequest extends BaseModel {
    @GeneratorRule(regex = "^[A-Za-z]+ [A-Za-z]+$")
    private String name;
}
