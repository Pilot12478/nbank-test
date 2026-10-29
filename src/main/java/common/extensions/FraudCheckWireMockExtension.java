
package common.extensions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import common.annotations.FraudCheckMock;
import org.apache.http.HttpStatus;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public class FraudCheckWireMockExtension implements BeforeEachCallback, ParameterResolver {
    public static final int PORT = 8089;
    public static final String LOCK = "fraud-check-mock";

    private static final WireMockServer SERVER = new WireMockServer(wireMockConfig().port(PORT));
    private static final ObjectMapper JSON = new ObjectMapper();

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        FraudCheckMock config = findConfig(context);

        startOnce();
        SERVER.resetAll();
        SERVER.stubFor(get(urlPathEqualTo("/health")).willReturn(ok()));
        ResponseDefinitionBuilder response = switch (config.fault()) {
            case NONE -> okJson(JSON.writeValueAsString(Map.of(
                    "status", config.status(),
                    "decision", config.decision(),
                    "riskScore", config.riskScore(),
                    "reason", config.reason(),
                    "requiresManualReview", config.requiresManualReview(),
                    "additionalVerificationRequired", config.additionalVerificationRequired())));
            case EMPTY_BODY -> aResponse().withStatus(HttpStatus.SC_OK);
            case HTTP_ERROR -> aResponse().withStatus(config.statusCode());
            case CONNECTION_RESET -> aResponse().withFixedDelay(config.delayMs());
        };
        SERVER.stubFor(post(urlPathEqualTo("/fraud-check")).willReturn(response));
    }

    private static synchronized void startOnce() {
        if (!SERVER.isRunning()) {
            SERVER.start();
            Runtime.getRuntime().addShutdownHook(new Thread(SERVER::stop));
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType() == FraudCheckMock.class;
    }

    @Override
    public @Nullable Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return findConfig(extensionContext);
    }

    public FraudCheckMock findConfig(ExtensionContext context) {
        return AnnotationSupport.findAnnotation(context.getTestMethod(), FraudCheckMock.class)
                .or(() -> AnnotationSupport.findAnnotation(context.getTestClass(), FraudCheckMock.class))
                .orElseThrow();
    }
}
