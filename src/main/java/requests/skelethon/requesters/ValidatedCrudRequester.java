package requests.skelethon.requesters;

import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.BaseModel;
import requests.skelethon.Endpoint;
import requests.skelethon.HttpRequest;
import requests.skelethon.interfaces.CrudEnpointInterface;

public class ValidatedCrudRequester<T extends BaseModel> extends HttpRequest implements CrudEnpointInterface {
    private final CrudRequester crudRequester;
    public ValidatedCrudRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification, Endpoint endpoint) {
        super(requestSpecification, responseSpecification, endpoint);
        this.crudRequester = new CrudRequester(requestSpecification, responseSpecification, endpoint);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T post(BaseModel baseModel) {
        return (T) crudRequester.post(baseModel).extract().as(endpoint.getResponseModel());
    }

    @Override
    @SuppressWarnings("unchecked")

    public T get() {
        return (T) crudRequester.get().extract().as(endpoint.getResponseModel());
    }

    @Override
    @SuppressWarnings("unchecked")

    public T update(long id, BaseModel baseModel) {
        return (T) crudRequester.update(id,baseModel).extract().as(endpoint.getResponseModel());
    }
    @SuppressWarnings("unchecked")
    public T update(BaseModel baseModel) {
        return (T) crudRequester.update(baseModel).extract().as(endpoint.getResponseModel());
    }

    @Override
    @SuppressWarnings("unchecked")

    public T delete(long id) {
        return (T) crudRequester.delete(id).extract().as(endpoint.getResponseModel());
    }
}
