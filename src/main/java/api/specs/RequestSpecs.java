package api.specs;

import api.configs.Config;
import com.google.j2objc.annotations.Property;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import api.steps.LoginSteps;
import api.steps.UserInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static api.specs.Headers.AUTHORIZATION;

public class RequestSpecs {
    private static final Map<String, String> authHeaders = new HashMap<>(Map.of());

    private static RequestSpecBuilder defaultReq() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .setBaseUri(Config.apiBaseUrl())
                .addFilters(List.of(new ResponseLoggingFilter()
                        , new RequestLoggingFilter()));
    }

    public static RequestSpecification unAuthReq() {
        return defaultReq()
                .build();
    }

    public static RequestSpecification adminAuthReq() {
        String auth;
        if (!authHeaders.containsKey(Config.adminLogin())) {
            auth = LoginSteps.login(Config.adminLogin(), Config.adminPassword());
            authHeaders.put(Config.adminLogin(), auth);
        } else auth = authHeaders.get(Config.adminLogin());

        return defaultReq()
                .addHeader(AUTHORIZATION, auth)
                .build();
    }

    public static RequestSpecification userAuthReq(UserInfo userInfo) {
        String auth;
        if (!authHeaders.containsKey(userInfo.getUsername())) {
            auth = LoginSteps.login(userInfo);
            authHeaders.put(userInfo.getUsername(), auth);
        } else auth = authHeaders.get(userInfo.getUsername());


        return defaultReq()
                .addHeader(AUTHORIZATION, auth)
                .build();
    }

    public static String getUserToken(UserInfo userInfo) {
        if (!authHeaders.containsKey(userInfo.getUsername())) {
            return LoginSteps.login(userInfo);
        } else return authHeaders.get(userInfo.getUsername());

    }
}
