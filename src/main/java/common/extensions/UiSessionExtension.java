package common.extensions;

import common.annotations.UiSession;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Optional;

import static ui.pages.BasePage.authAsUser;

public class UiSessionExtension extends UserSessionExtension {

    @Override
    protected Optional<SessionSpec> findSession(ExtensionContext context) {
        return AnnotationSupport.findAnnotation(context.getTestMethod(), UiSession.class)
                .map(s -> new SessionSpec(s.accounts(), s.balance()));
    }

    @Override
    protected void onUserCreated(TestUser user) {
        authAsUser(user.user());
    }
}
