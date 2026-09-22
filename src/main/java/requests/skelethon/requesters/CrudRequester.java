package requests.skelethon.requesters;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.BaseModel;
import requests.skelethon.Endpoint;
import requests.skelethon.HttpRequest;
import requests.skelethon.interfaces.CrudEnpointInterface;

import static io.restassured.RestAssured.given;

public class CrudRequester extends HttpRequest implements CrudEnpointInterface {
    public CrudRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification, Endpoint endpoint) {
        super(requestSpecification, responseSpecification, endpoint);
    }

    @Override
    public ValidatableResponse post(BaseModel baseModel) {
        var body = baseModel == null ? "" : baseModel;
        return given()
                .spec(requestSpecification)
                .body(body)
                .post(endpoint.getUrl())
                .then()
                .spec(responseSpecification);
    }

    @Override
    public ValidatableResponse get() {
        return  given()
                .spec(requestSpecification)
                .get(endpoint.getUrl())
                .then()
                .spec(responseSpecification);
    }

    @Override
    public ValidatableResponse update(long id, BaseModel baseModel) {
        return given()
                .spec(requestSpecification)
                .pathParam("id",id)
                .body(baseModel)
                .put(endpoint.getUrl())
                .then()
                .spec(responseSpecification);
    }

    public ValidatableResponse update(BaseModel baseModel) {
        return given()
                .spec(requestSpecification)
                .body(baseModel)
                .put(endpoint.getUrl())
                .then()
                .spec(responseSpecification);
    }

    @Override
    public ValidatableResponse delete(long id) {
        return given()
                .spec(requestSpecification)
                .pathParam("id",id)
                .delete(endpoint.getUrl())
                .then()
                .spec(responseSpecification);
    }
}
