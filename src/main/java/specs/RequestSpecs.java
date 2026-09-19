package specs;

import configs.Config;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import models.LoginUserModelRequest;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;

import java.util.List;

public class RequestSpecs {
    private static RequestSpecBuilder defaultReq() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .setBaseUri(Config.getProperty("server")+Config.getProperty("apiVersion"))
                .addFilters(List.of(new ResponseLoggingFilter()
                        , new RequestLoggingFilter()));
    }

    public static RequestSpecification unAuthReq() {
        return defaultReq()
                .build();
    }

    public static RequestSpecification adminAuthReq() {
        return defaultReq()
                .addHeader("Authorization", "Basic YWRtaW46YWRtaW4=")
                .build();
    }

    public static RequestSpecification userAuthReq(String username, String password) {
        String auth = new CrudRequester(RequestSpecs.adminAuthReq(), ResponseSpecs.ok(), Endpoint.LOGIN)
                .post(LoginUserModelRequest.builder()
                        .username(username)
                        .password(password)
                        .build()).extract()
                .header("Authorization");


        return defaultReq()
                .addHeader("Authorization", auth)
                .build();
    }
}
