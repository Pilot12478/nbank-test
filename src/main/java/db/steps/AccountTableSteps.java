package db.steps;

import db.models.AccountsDao;
import org.jooq.DSLContext;

import static com.example.jooq.generated.tables.Accounts.ACCOUNTS;


public class AccountTableSteps {

    public static AccountsDao getAccountByNumber(DSLContext dslContext, int accId) {
        return dslContext.selectFrom(ACCOUNTS)
                .where(ACCOUNTS.ACCOUNT_NUMBER.eq("ACC" + accId))
                .fetchOneInto(AccountsDao.class);
    }


}