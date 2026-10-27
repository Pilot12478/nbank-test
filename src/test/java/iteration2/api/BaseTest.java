package iteration2.api;

import common.annotations.ThreadTiming;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
@ThreadTiming
public class BaseTest {
    protected SoftAssertions softly;

    @BeforeEach
    public void setUpTest() {
        this.softly = new SoftAssertions();
    }@AfterEach
    public void afterTest(){
        softly.assertAll();
    }
}
