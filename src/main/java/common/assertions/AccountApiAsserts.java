package common.assertions;

import common.extensions.TestUser;
import org.assertj.core.api.SoftAssertions;

import static api.steps.AccountSteps.getAccountBalance;
import static org.assertj.core.api.Assertions.offset;

public class AccountApiAsserts {
    private final SoftAssertions softly;
    private final double balance;

    private AccountApiAsserts(SoftAssertions softly, double balance) {
        this.softly = softly;
        this.balance = balance;
    }

    public static AccountApiAsserts assertThatAccount(SoftAssertions softly, TestUser user) {
        return assertThatAccount(softly, user, user.accountId());
    }

    public static AccountApiAsserts assertThatAccount(SoftAssertions softly, TestUser user, int accountId) {
        return new AccountApiAsserts(softly, getAccountBalance(user.credentials(), accountId));
    }

    public AccountApiAsserts hasBalance(double expected) {
        softly.assertThat(balance).isCloseTo(expected, offset(0.001));
        return this;
    }
}
