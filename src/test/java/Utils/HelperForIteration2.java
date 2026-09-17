package Utils;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.ResponseSpecification;
import models.*;
import requests.*;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import requests.skelethon.requesters.ValidatedCrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import static Utils.TestDataGenerator.generateUserName;
import static Utils.TestDataGenerator.getDefaultPassword;


public class HelperForIteration2 {

    public static CreateUserModelResponse createUser(String userName, String password, String role) {
        return new ValidatedCrudRequester<CreateUserModelResponse>(RequestSpecs.adminAuthReq(),
                ResponseSpecs.created(), Endpoint.ADMIN_USER)
                .post(CreateUserModelRequest.builder()
                        .username(userName)
                        .password(password)
                        .role(role)
                        .build());

    }


    public static CreateAccountModelResponse createAccount(String userName, String password) {
        return new ValidatedCrudRequester<CreateAccountModelResponse>(RequestSpecs.userAuthReq(userName, password),
                ResponseSpecs.created(), Endpoint.ACCOUNTS)
                .post(null);


    }

    public static AccountInfo createUserAndAccount() {
        String username = generateUserName();
        String password = getDefaultPassword();
        String role = UserRole.USER.toString();

        int id = createUser(username, password, role).getId();
        CreateAccountModelResponse response = createAccount(username, password);
        int accountId = response.getId();
        return new AccountInfo(username, password, accountId, id);
    }

    public static ValidatableResponse depositAccountNegative(AccountInfo info, double sum, ResponseSpecification responseSpecification) {
        return new CrudRequester(RequestSpecs.userAuthReq(info.getUsername(), info.getPassword()), responseSpecification, Endpoint.DEPOSIT)
                .post(DepositModelRequest
                        .builder()
                        .id(info.getAccountId())
                        .balance(sum)
                        .build()

                );


    }

    public static DepositModelResponse depositAccountPositive(AccountInfo info, double sum, ResponseSpecification responseSpecification) {
        return new ValidatedCrudRequester<DepositModelResponse>(RequestSpecs.userAuthReq(info.getUsername(), info.getPassword()), responseSpecification, Endpoint.DEPOSIT)
                .post(DepositModelRequest
                        .builder()
                        .id(info.getAccountId())
                        .balance(sum)
                        .build()

                );


    }


    public static UserModelResponseProfile getUserAccount(AccountInfo info) {
        return new ValidatedCrudRequester<UserModelResponseProfile>(
                RequestSpecs.userAuthReq(info.getUsername(), info.getPassword()), ResponseSpecs.ok(), Endpoint.USER_PROFILE)
                .get();
    }


    public static CreateTransferModelResponse createTransferPositive(AccountInfo senderAccountInfo,
                                                                     double sum, int receiverAccountId,
                                                                     ResponseSpecification responseSpecification) {
        return new ValidatedCrudRequester<CreateTransferModelResponse>(RequestSpecs.userAuthReq(senderAccountInfo.getUsername(),
                senderAccountInfo.getPassword()), responseSpecification, Endpoint.TRANSFER)
                .post(CreateTransferModelRequest
                        .builder()
                        .amount(sum)
                        .senderAccountId(senderAccountInfo.getAccountId())
                        .receiverAccountId(receiverAccountId)
                        .build());
    }

    public static ValidatableResponse createTransferNegative(AccountInfo senderAccountInfo,
                                                             double sum, int receiverAccountId,
                                                             ResponseSpecification responseSpecification) {
        return new CrudRequester(RequestSpecs.userAuthReq(senderAccountInfo.getUsername(),
                senderAccountInfo.getPassword()), responseSpecification, Endpoint.TRANSFER)
                .post(CreateTransferModelRequest
                        .builder()
                        .amount(sum)
                        .senderAccountId(senderAccountInfo.getAccountId())
                        .receiverAccountId(receiverAccountId)
                        .build());
    }

    public static double getAccountBalance(AccountInfo info) {
        return getUserAccount(info)
                .getAccounts().stream()
                .filter(a -> a.getId() == info.getAccountId())
                .findFirst()
                .orElseThrow(() -> new AssertionError("Account not found: " + info.getAccountId()))
                .getBalance();
    }

    public static double getAccountBalance(AccountInfo info, int accountId) {
        return getUserAccount(info)
                .getAccounts().stream()
                .filter(a -> a.getId() == accountId)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Account not found: " + accountId))
                .getBalance();
    }

    public static ValidatableResponse updateUserNameNegative(AccountInfo accountInfo, String name, ResponseSpecification responseSpecification) {
        return new CrudRequester(RequestSpecs.userAuthReq(
                accountInfo.getUsername(), accountInfo.getPassword()), responseSpecification, Endpoint.USER_NAME)
                .update(UpdateUserNameModelRequest
                        .builder()
                        .name(name)
                        .build());
    }

    public static UpdateUserNameModelResponse updateUserNamePositive(AccountInfo accountInfo, String name, ResponseSpecification responseSpecification) {
        return new ValidatedCrudRequester<UpdateUserNameModelResponse>(RequestSpecs.userAuthReq(
                accountInfo.getUsername(), accountInfo.getPassword()), responseSpecification, Endpoint.USER_NAME)
                .update(UpdateUserNameModelRequest
                        .builder()
                        .name(name)
                        .build());
    }

    public static void deleteUser(AccountInfo accountInfo) {
        new CrudRequester(RequestSpecs.adminAuthReq(), ResponseSpecs.ok(), Endpoint.DELETE_PROFILE).delete(accountInfo.getUserId());
    }

}
