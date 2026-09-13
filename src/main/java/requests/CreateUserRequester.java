package requests;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.BaseModel;
import org.apache.http.HttpStatus;

import static io.restassured.RestAssured.given;

public class CreateUserRequester extends Request implements PostRequest{
    public CreateUserRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    @Override
    public ValidatableResponse send(BaseModel baseModel) {
        return given()
                .spec(requestSpecification)
                .body(baseModel)
                .post("/api/v1/admin/users")
                .then()
                .spec(responseSpecification);
    }
}
