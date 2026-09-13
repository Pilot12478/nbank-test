package requests;

import io.restassured.response.ValidatableResponse;
import models.BaseModel;

public interface GetRequest {
    ValidatableResponse send();
}
