package common.extensions;

import api.steps.UserInfo;

import java.util.List;

public record TestUser(UserInfo credentials, List<Integer> accounts) {

    public int accountId() {
        return accounts.getFirst();
    }

    public int accountId(int index) {
        return accounts.get(index);
    }
}
