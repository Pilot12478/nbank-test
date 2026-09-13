package requests;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.BaseModel;

import static io.restassured.RestAssured.given;

public class DepositRequester extends Request implements PostRequest {
    public DepositRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    @Override
    public ValidatableResponse send(BaseModel baseModel) {
        return     given()
                .spec(requestSpecification)
                .body(baseModel)
                .post("/api/v1/accounts/deposit")
                .then()
                .spec(responseSpecification);
    }
}
