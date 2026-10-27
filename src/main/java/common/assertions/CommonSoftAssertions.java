package common.assertions;

import org.assertj.core.api.SoftAssertions;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.offset;

public class CommonSoftAssertions {
    private final SoftAssertions softly;

    public CommonSoftAssertions(SoftAssertions softly) {
        this.softly = softly;
    }

    public static CommonSoftAssertions assertThat(SoftAssertions softly) {
        return new CommonSoftAssertions(softly);
    }

    public CommonSoftAssertions isEqualTo(Object actual, Object expected) {
        softly.assertThat(actual).isEqualTo(expected);
        return this;

    }
    public CommonSoftAssertions isCloseTo(double actual, double expected) {
        softly.assertThat(actual).isCloseTo(expected, offset(0.001));
        return this;

    }


    public CommonSoftAssertions isNull(Object object) {
        softly.assertThat(object).isNull();
        return this;

    }
}
