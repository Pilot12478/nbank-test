package Utils;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.ResponseSpecification;
import models.*;
import requests.*;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.List;


public class HelperForIteration2 {
    public static final String BASE_URL = "http://localhost:4111";

    public static String createUser(String userName, String password, String role) {
        return new CreateUserRequester(RequestSpecs.adminAuthReq(), ResponseSpecs.created())
                .send(CreateUserModelRequest.builder()
                        .username(userName)
                        .password(password)
                        .role(role)
                        .build())
                .extract()
                .header("Authorization");

    }


    public static int createAccount(String userName, String password) {
        return new CreateAccountRequester(RequestSpecs.userAuthReq(userName, password), ResponseSpecs.created())
                .send(null).extract().as(CreateAccountModelResponse.class).getId();


    }

    public static ValidatableResponse depositAccount(String userName, String password, int accountId, double sum, ResponseSpecification responseSpecification) {
        return new DepositRequester(RequestSpecs.userAuthReq(userName, password), responseSpecification)
                .send(DepositModelRequest
                        .builder()
                        .id(accountId)
                        .balance(sum)
                        .build()

                );


    }
    public static ValidatableResponse getUserAccount(String userName, String password){
        return new GetUserProfileRequester(
                RequestSpecs.userAuthReq(userName, password), ResponseSpecs.ok())
                .send(null);
    }


    public static ValidatableResponse createTransfer(String userName, String password,double sum, int senderAccountId, int receiverAccountId,ResponseSpecification responseSpecification){
        return new CreateTransferRequester(RequestSpecs.userAuthReq(userName,password),responseSpecification)
                .send(CreateTransferModelRequest
                        .builder()
                        .amount(sum)
                        .senderAccountId(senderAccountId)
                        .receiverAccountId(receiverAccountId)
                        .build());
    }
}
