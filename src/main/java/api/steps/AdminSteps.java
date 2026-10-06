package api.steps;

import api.generators.RandomModelGenerator;
import api.models.CreateUserModelRequest;
import api.models.CreateUserModelResponse;
import api.models.UserRole;
import api.skelethon.Endpoint;
import api.skelethon.requesters.CrudRequester;
import api.skelethon.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

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
