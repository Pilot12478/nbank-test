package requests;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.CreateTransferModelRequest;
import org.apache.http.HttpStatus;

import static io.restassured.RestAssured.given;

public class CreateTransferRequester extends Request<CreateTransferModelRequest> {
    public CreateTransferRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    @Override
    public ValidatableResponse send(CreateTransferModelRequest body) {
        return  given()
                .spec(requestSpecification)
                .body(body)
                .post("/api/v1/accounts/transfer")
                .then()
                .spec(responseSpecification);
    }
}
