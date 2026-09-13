package specs;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;

public class ResponseSpecs {
    public static ResponseSpecification created(){
        return new ResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_CREATED)
                .build();
    }

    public static ResponseSpecification ok(){
        return new ResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_OK)
                .build();
    }

    public static ResponseSpecification badRequest(){
        return new ResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_BAD_REQUEST)
                .build();
    }
    public static ResponseSpecification forbidden(){
        return new ResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_FORBIDDEN)
                .build();
    }

}
