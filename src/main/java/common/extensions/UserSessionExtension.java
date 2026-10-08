package common.extensions;

import api.steps.AccountSteps;
import api.steps.AdminSteps;
import api.steps.DepositSteps;
import api.steps.UserInfo;
import common.annotations.ExtraUser;
import common.annotations.UserSession;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ui.pages.BasePage.authAsUser;


public class UserSessionExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {
    private static final ExtensionContext.Namespace NS = ExtensionContext.Namespace.create(UserSessionExtension.class);
    private static final double MAX_DEPOSIT = 5000;
    private static final String CREATED = "createdUsers";

    public void beforeEach(ExtensionContext context) {
        UserSession session = findSession(context);
        if (session == null) return;

        TestUser user = createTestUser(context, session.accounts(), session.balance());
        authAsUser(user.user());
        context.getStore(NS).put("testUser", user);
    }

    @Override
    public void afterEach(ExtensionContext context) {
        List<UserInfo> created = context.getStore(NS).get(CREATED, List.class);
        if (created == null) return;
        for (UserInfo user : created) {
            AdminSteps.deleteUser(user);
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext p, ExtensionContext c) {
        return p.getParameter().getType() == TestUser.class;
    }

    @Override
    public Object resolveParameter(ParameterContext p, ExtensionContext c) {
        Optional<ExtraUser> extra = p.findAnnotation(ExtraUser.class);
        if (extra.isPresent()) {
            return createTestUser(c, extra.get().accounts(), extra.get().balance());
        }
        return c.getStore(NS).get("testUser", TestUser.class);
    }

    private UserSession findSession(ExtensionContext context) {
        return AnnotationSupport.findAnnotation(context.getTestMethod(), UserSession.class)
                .orElse(null);
    }

    @SuppressWarnings("unchecked")
    private TestUser createTestUser(ExtensionContext context, int accountsCount, double balance) {
        UserInfo user = AdminSteps.createUser();
        context.getStore(NS)
                .getOrComputeIfAbsent(CREATED, k -> new ArrayList<UserInfo>(), List.class)
                .add(user);

        List<Integer> accounts = new ArrayList<>();
        for (int i = 0; i < accountsCount; i++) {
            accounts.add(AccountSteps.createAccount(user));
        }
        double left = balance;
        while (left > 0) {
            double part = Math.min(left, MAX_DEPOSIT);
            DepositSteps.depositAccount(user, accounts.get(0), part);
            left -= part;
        }
        return new TestUser(user, accounts);
    }
}
