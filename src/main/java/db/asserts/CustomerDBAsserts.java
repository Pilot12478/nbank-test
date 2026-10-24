package db.asserts;

import db.models.CustomerDao;
import db.steps.CustomerTableSteps;
import org.assertj.core.api.SoftAssertions;
import org.jooq.DSLContext;

public class CustomerDBAsserts {
    private final SoftAssertions softly;
    private final CustomerDao customer;

    public CustomerDBAsserts(SoftAssertions softly, CustomerDao customerDao) {
        this.softly = softly;
        this.customer = customerDao;
    }

    public static CustomerDBAsserts assertThatCustomer(SoftAssertions softly, DSLContext dslContext, long Id) {
        return new CustomerDBAsserts(softly, CustomerTableSteps.getCustomerById(dslContext, Id));
    }

    public CustomerDBAsserts hasName(String expectedName) {
        softly.assertThat(customer.getName()).isEqualTo(expectedName);
        return this;

    }
}
