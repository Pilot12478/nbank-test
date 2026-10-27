package db.steps;

import db.models.CustomerDao;
import org.jooq.DSLContext;

import static com.example.jooq.generated.tables.Customers.CUSTOMERS;

public class CustomerTableSteps {
    public static CustomerDao getCustomerById(DSLContext dslContext, long Id) {
        return dslContext.selectFrom(CUSTOMERS)
                .where(CUSTOMERS.ID.eq(Id))
                .fetchOneInto(CustomerDao.class);
    }

}
