package requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.RestAssured.delete;
import static io.restassured.RestAssured.given;

public class DeleteUserRequester extends RequestNoBody{
    private final int userId;
    public DeleteUserRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification, int userId) {
        super(requestSpecification, responseSpecification);
        this.userId = userId;
    }
    @Override
    public ValidatableResponse send() {
        return given()
                .spec(requestSpecification)
                .pathParam("id", userId)
                .delete("/api/v1/admin/users/{id}")
                .then()
                .spec(responseSpecification);
    }
}
