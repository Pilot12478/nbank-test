package db.asserts;

import db.models.AccountsDao;
import db.steps.AccountTableSteps;
import org.assertj.core.api.SoftAssertions;
import org.jooq.DSLContext;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class AccountDbAsserts {
    private final SoftAssertions softly;
    private final AccountsDao account;
    public AccountDbAsserts(SoftAssertions softly, AccountsDao account) {
        this.softly = softly;
        this.account = account;
    }
    public static AccountDbAsserts assertThatAccount(SoftAssertions softly, DSLContext dslContext, int accId) {
        return new AccountDbAsserts(softly, AccountTableSteps.getAccountByNumber(dslContext,accId));
    }
    public AccountDbAsserts hasBalance(double expected) {
        BigDecimal expectedValue = BigDecimal.valueOf(expected).setScale(2, RoundingMode.HALF_UP);
        softly.assertThat(account.getBalance()).isEqualByComparingTo(expectedValue);
        return this;
    }
}
