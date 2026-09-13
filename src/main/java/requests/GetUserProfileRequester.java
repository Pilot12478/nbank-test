package requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.RestAssured.given;

public class GetUserProfileRequester extends Request implements GetRequest{
    public GetUserProfileRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    @Override
    public ValidatableResponse send() {
        return given()
                .spec(requestSpecification)
                .get("/api/v1/customer/profile")
                .then()
                .spec(responseSpecification);

        /*
            .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", userAuthToken)
                .get(BASE_URL + "/api/v1/customer/profile")
         */
    }
}
