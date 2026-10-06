package steps;

import generators.RandomModelGenerator;
import models.CreateUserModelRequest;
import models.CreateUserModelResponse;
import models.UserRole;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import requests.skelethon.requesters.ValidatedCrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

public class AdminSteps {
    public static UserInfo createUser() {
        CreateUserModelRequest request = RandomModelGenerator.generate(CreateUserModelRequest.class);
        request.setRole(UserRole.USER.toString());
        CreateUserModelResponse response = new ValidatedCrudRequester<CreateUserModelResponse>(
                RequestSpecs.adminAuthReq(),
                ResponseSpecs.created(),
                Endpoint.ADMIN_USER
        ).post(request);

        return new UserInfo(request.getUsername(), request.getPassword(), response.getId());
    }

    public static void deleteUser(UserInfo userInfo) {
        if (userInfo == null) return;
        new CrudRequester(RequestSpecs.adminAuthReq(), ResponseSpecs.ok(), Endpoint.DELETE_PROFILE).delete(userInfo.getUserId());
    }
}
