package requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.BaseModel;
import models.LoginUserModelRequest;

import static io.restassured.RestAssured.given;

public class LoginRequester extends Request<LoginUserModelRequest> {
    public LoginRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    public ValidatableResponse send(LoginUserModelRequest body) {
        return given()
                .spec(requestSpecification)
                .body(body)
                .post("/api/v1/auth/login")
                .then()
                .spec(responseSpecification);
    }


}
