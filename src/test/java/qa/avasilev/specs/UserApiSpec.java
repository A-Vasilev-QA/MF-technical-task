package qa.avasilev.specs;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.filter.log.LogDetail.*;
import static io.restassured.http.ContentType.JSON;
import static qa.avasilev.helpers.CustomAllureListener.withCustomTemplates;

public class UserApiSpec {
    public static RequestSpecification requestSpec = new RequestSpecBuilder()
            .addFilter(withCustomTemplates())
            .log(URI)
            .log(HEADERS)
            .log(BODY)
            .setContentType(JSON)
            .build();

    public static ResponseSpecification responseSpec = new ResponseSpecBuilder()
            .expectContentType(JSON)
            .log(STATUS)
            .log(BODY)
            .build();
}
