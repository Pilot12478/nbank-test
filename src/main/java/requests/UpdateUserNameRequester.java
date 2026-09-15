package requests;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.UpdateUserNameModelRequest;
import org.apache.http.HttpStatus;

import static io.restassured.RestAssured.given;

public class UpdateUserNameRequester extends Request<UpdateUserNameModelRequest> {

    public UpdateUserNameRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    @Override
    public ValidatableResponse send(UpdateUserNameModelRequest body) {
        return given()
                .spec(requestSpecification)
                .body(body)
                .put("/api/v1/customer/profile")
                .then()
                .spec(responseSpecification);
    }
}
