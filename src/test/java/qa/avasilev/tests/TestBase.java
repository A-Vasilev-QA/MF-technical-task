package qa.avasilev.tests;

import com.github.javafaker.Faker;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;

import static qa.avasilev.tests.TestData.BASE_URI;

public abstract class TestBase {
    protected final Faker faker = new Faker();
    protected SoftAssertions assertions = new SoftAssertions();

    @BeforeAll
    @Step("Setting up")
    public static void setUp() {
        RestAssured.baseURI = BASE_URI;
    }
}
