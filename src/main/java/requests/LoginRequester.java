package requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.BaseModel;

import static io.restassured.RestAssured.given;

public class LoginRequester extends Request implements PostRequest{
    public LoginRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    @Override
    public ValidatableResponse send(BaseModel baseModel) {
        return given()
                .spec(requestSpecification)
                .body(baseModel)
                .post("/api/v1/auth/login")
                .then()
                .spec(responseSpecification);
    }
}
