package requests.skelethon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import models.*;

@AllArgsConstructor
@Getter
public enum Endpoint {

    ADMIN_USER("/admin/users",
            CreateUserModelRequest.class,
            CreateUserModelResponse.class),
    ACCOUNTS("/accounts",
            BaseModel.class,
            CreateAccountModelResponse.class
    ),
    LOGIN("/auth/login",
            LoginUserModelRequest.class,
            LoginUserModelResponse.class),
    USER_PROFILE("/customer/profile",
            BaseModel.class,
            UserModelResponseProfile.class
    ),
    DEPOSIT("/accounts/deposit",
            DepositModelRequest.class,
            DepositModelResponse.class),
    TRANSFER("/accounts/transfer",
            CreateTransferModelRequest.class,
            CreateTransferModelResponse.class),
    USER_NAME("/customer/profile",
            UpdateUserNameModelRequest.class,
            UpdateUserNameModelResponse.class
    ),
    DELETE_PROFILE("/admin/users/{id}",
            BaseModel.class,
            BaseModel.class
    );

    private final String url;
    private final Class<? extends BaseModel> requestModel;
    private final Class<? extends BaseModel> responseModel;
}
