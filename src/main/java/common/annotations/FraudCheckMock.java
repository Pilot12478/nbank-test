package common.annotations;

import com.github.tomakehurst.wiremock.http.Fault;
import common.extensions.FraudCheckWireMockExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.ResourceLock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@ExtendWith(FraudCheckWireMockExtension.class)
@ResourceLock(FraudCheckWireMockExtension.LOCK)
public @interface FraudCheckMock {
    String status() default "SUCCESS";
    String decision() default "APPROVED";
    double riskScore() default 0.1;
    String reason() default "Mocked fraud check";
    boolean requiresManualReview() default false;
    boolean additionalVerificationRequired() default false;
    int delayMs() default 0;
    int statusCode() default 200;
    Fault fault() default Fault.NONE;
    enum Fault { NONE, HTTP_ERROR, EMPTY_BODY,CONNECTION_RESET }
}
