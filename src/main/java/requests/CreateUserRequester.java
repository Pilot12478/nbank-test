package requests;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.BaseModel;
import models.CreateUserModelRequest;
import org.apache.http.HttpStatus;

import static io.restassured.RestAssured.given;

public class CreateUserRequester extends Request<CreateUserModelRequest> {
    public CreateUserRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    public ValidatableResponse send(CreateUserModelRequest body) {
        return given()
                .spec(requestSpecification)
                .body(body)
                .post("/api/v1/admin/users")
                .then()
                .spec(responseSpecification);
    }


}
