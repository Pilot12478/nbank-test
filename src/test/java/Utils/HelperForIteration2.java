package Utils;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.ResponseSpecification;
import models.*;
import requests.*;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import static Utils.TestDataGenerator.generateUserName;
import static Utils.TestDataGenerator.getDefaultPassword;


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

    public static AccountInfo createUserAndAccount() {
        String username = generateUserName();
        String password = getDefaultPassword();
        String role = UserRole.USER.toString();
        createUser(username, password, role);
        int accountId = createAccount(username, password);
        return new AccountInfo(username, password, accountId);
    }

    public static ValidatableResponse depositAccount(AccountInfo info, double sum, ResponseSpecification responseSpecification) {
        return new DepositRequester(RequestSpecs.userAuthReq(info.getUsername(), info.getPassword()), responseSpecification)
                .send(DepositModelRequest
                        .builder()
                        .id(info.getAccountId())
                        .balance(sum)
                        .build()

                );


    }
    public static ValidatableResponse depositAccount(String username, String password, int accountId, double sum, ResponseSpecification responseSpecification) {
        return new DepositRequester(RequestSpecs.userAuthReq(username, password), responseSpecification)
                .send(DepositModelRequest
                        .builder()
                        .id(accountId)
                        .balance(sum)
                        .build());
    }



    public static ValidatableResponse getUserAccount(AccountInfo info) {
        return new GetUserProfileRequester(
                RequestSpecs.userAuthReq(info.getUsername(), info.getPassword()), ResponseSpecs.ok())
                .send(null);
    }


    public static ValidatableResponse createTransfer(AccountInfo senderAccountInfo, double sum, int receiverAccountId, ResponseSpecification responseSpecification) {
        return new CreateTransferRequester(RequestSpecs.userAuthReq(senderAccountInfo.getUsername(), senderAccountInfo.getPassword()), responseSpecification)
                .send(CreateTransferModelRequest
                        .builder()
                        .amount(sum)
                        .senderAccountId(senderAccountInfo.getAccountId())
                        .receiverAccountId(receiverAccountId)
                        .build());
    }
    public static double getAccountBalance(AccountInfo info) {
        return getUserAccount(info)
                .extract().as(UserModelResponseProfile.class)
                .getAccounts().stream()
                .filter(a -> a.getId() == info.getAccountId())
                .findFirst()
                .orElseThrow(() -> new AssertionError("Account not found: " + info.getAccountId()))
                .getBalance();
    }
    public static double getAccountBalance(AccountInfo info, int accountId) {
        return getUserAccount(info)
                .extract().as(UserModelResponseProfile.class)
                .getAccounts().stream()
                .filter(a -> a.getId() == accountId)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Account not found: " + accountId))
                .getBalance();
    }
    public static ValidatableResponse updateUserName(AccountInfo accountInfo,String name, ResponseSpecification responseSpecification){
        return new UpdateUserNameRequester(RequestSpecs.userAuthReq(
                accountInfo.getUsername(), accountInfo.getPassword()), responseSpecification)
                .send(UpdateUserNameModelRequest
                        .builder()
                        .name(name)
                        .build());
    }
}
