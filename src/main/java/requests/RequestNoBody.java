package requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public abstract class RequestNoBody {
    protected RequestSpecification requestSpecification;
    protected ResponseSpecification responseSpecification;

    public RequestNoBody(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        this.requestSpecification = requestSpecification;
        this.responseSpecification = responseSpecification;
    }

    public abstract ValidatableResponse send();
}
