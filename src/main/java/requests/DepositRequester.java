package requests;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.BaseModel;
import models.DepositModelRequest;

import static io.restassured.RestAssured.given;

public class DepositRequester extends Request<DepositModelRequest> {
    public DepositRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    public ValidatableResponse send(DepositModelRequest body) {
        return given()
                .spec(requestSpecification)
                .body(body)
                .post("/api/v1/accounts/deposit")
                .then()
                .spec(responseSpecification);
    }

}
