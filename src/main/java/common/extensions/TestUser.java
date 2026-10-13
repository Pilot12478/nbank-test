package common.extensions;

import api.steps.UserInfo;

import java.util.List;

public record TestUser(UserInfo user, List<Integer> accounts) {

}