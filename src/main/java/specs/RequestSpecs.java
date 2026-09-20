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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestSpecs {
    private static  Map<String, String> authHeaders = new HashMap<>(Map.of("admin","Basic YWRtaW46YWRtaW4="));

    private static RequestSpecBuilder defaultReq() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .setBaseUri(Config.getProperty("server") + Config.getProperty("apiVersion"))
                .addFilters(List.of(new ResponseLoggingFilter()
                        , new RequestLoggingFilter()));
    }

    public static RequestSpecification unAuthReq() {
        return defaultReq()
                .build();
    }

    public static RequestSpecification adminAuthReq() {
        return defaultReq()
                .addHeader("Authorization", authHeaders.get("admin"))
                .build();
    }

    public static RequestSpecification userAuthReq(String username, String password) {
        String auth;
        if (!authHeaders.containsKey(username)) {
            auth = new CrudRequester(RequestSpecs.adminAuthReq(), ResponseSpecs.ok(), Endpoint.LOGIN)
                    .post(LoginUserModelRequest.builder()
                            .username(username)
                            .password(password)
                            .build()).extract()
                    .header("Authorization");
            authHeaders.put(username, auth);
        } else auth = authHeaders.get(username);


        return defaultReq()
                .addHeader("Authorization", auth)
                .build();
    }
}
