package requests;

import io.restassured.response.ValidatableResponse;
import models.BaseModel;

public interface PostRequest {
    ValidatableResponse send(BaseModel baseModel);
}
