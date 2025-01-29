package qa.avasilev.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import qa.avasilev.models.CreateUserNegativeResponseModel;
import qa.avasilev.models.CreateUserPositiveResponseModel;
import qa.avasilev.models.CreateUserRequestModel;
import qa.avasilev.models.GetUserResponseModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static java.lang.String.format;
import static qa.avasilev.specs.UserApiSpec.requestSpec;
import static qa.avasilev.specs.UserApiSpec.responseSpec;
import static qa.avasilev.tests.TestData.*;

public class UserApiTests extends TestBase {
    @Test
    @DisplayName("Unique new user can be successfully created and read")
    void verifyValidUniqueUserSuccessfullyCreated() {
        CreateUserRequestModel newValidUser =
                new CreateUserRequestModel(faker.name().username(),
                        faker.internet().emailAddress(),
                        faker.internet().password());

        CreateUserPositiveResponseModel postResponse =
                step(format("Sending POST for user creation with Username '%s', email '%s', password '%s'",
                        newValidUser.getUsername(), newValidUser.getEmail(), newValidUser.getPassword()), () ->
                        given(requestSpec)
                                .multiPart("username", newValidUser.getUsername())
                                .multiPart("email", newValidUser.getEmail())
                                .multiPart("password", newValidUser.getPassword())
                        .when()
                                .post(CREATE_ENDPOINT)
                        .then()
                                .spec(responseSpec)
                                .statusCode(200)
                                .extract().as(CreateUserPositiveResponseModel.class)
                );

        step("Validating POST response", () -> {
            assertions.assertThat(postResponse.isSuccess()).isTrue();
            assertions.assertThat(postResponse.getMessage()).isEqualTo(USER_CREATED_POSITIVE_MESSAGE);
            assertions.assertThat(postResponse.getDetails().getId()).isGreaterThan(0);
            assertions.assertThat(postResponse.getDetails().getUsername()).isEqualTo(newValidUser.getUsername());
            assertions.assertThat(postResponse.getDetails().getEmail()).isEqualTo(newValidUser.getEmail());
            assertions.assertThat(postResponse.getDetails().getCreated_at()).isNotBlank();
            assertions.assertThat(postResponse.getDetails().getUpdated_at()).isNotBlank();
            assertions.assertAll();
        });

        GetUserResponseModel[] getResponse =
                step(format("Checking that new user with id '%d' can be retrieved from the list",
                        postResponse.getDetails().getId()), () ->
                        given(requestSpec)
                                .get(format("%s?id=%d", READ_ENDPOINT, postResponse.getDetails()
                                        .getId()))
                        .then()
                                .spec(responseSpec)
                                .statusCode(200)
                                .extract().as(GetUserResponseModel[].class)
                );

        step("Validating the user data in GET response", () -> {
            assertions.assertThat(getResponse).hasSize(1);
            assertions.assertThat(getResponse[0]).isEqualTo(postResponse.getDetails());
            assertions.assertAll();
        });
    }

    @Test
    @DisplayName("User list can be retrieved and is correct")
    void verifyUserListSuccessfullyRead() {
        List<CreateUserRequestModel> newUsers = new ArrayList<>();
        step("Adding 5 users to the list", () -> {
            for (int i = 0; i < 5; i++) {
                CreateUserRequestModel user = new CreateUserRequestModel(
                        faker.name().username(),
                        faker.internet().emailAddress(),
                        faker.internet().password()
                );
                newUsers.add(user);

                given(requestSpec)
                        .multiPart("username", user.getUsername())
                        .multiPart("email", user.getEmail())
                        .multiPart("password", user.getPassword())
                .when()
                        .post(CREATE_ENDPOINT)
                .then()
                        .spec(responseSpec)
                        .statusCode(200);
            }
        });

        GetUserResponseModel[] response =
                step("Getting the list of users using GET", () ->
                        given(requestSpec)
                                .get(READ_ENDPOINT)
                        .then()
                                .spec(responseSpec)
                                .statusCode(200)
                                .extract().as(GetUserResponseModel[].class)
                );

        step("Checking that there are 5 or more users in the list", () -> {
            assertions.assertThat(response.length).isGreaterThanOrEqualTo(5);
            assertions.assertAll();
        });

        step("Checking that added users are present in the response", () -> {
            List<String> responseUsernames = Arrays.stream(response)
                    .map(GetUserResponseModel::getUsername)
                    .toList();

            newUsers.forEach(user -> {
                    assertions.assertThat(responseUsernames).contains(user.getUsername());
                    assertions.assertAll();
            });
        });
    }

    @Test
    @DisplayName("User can't be created with non-unique username")
    void verifyNonUniqueUserNameNotCreated() {
        CreateUserRequestModel newUser =
                new CreateUserRequestModel(faker.name().username(),
                        faker.internet().emailAddress(),
                        faker.internet().password());

        CreateUserRequestModel nonUniqueUser =
                new CreateUserRequestModel(newUser.getUsername(),
                        faker.internet().emailAddress(),
                        faker.internet().password());

        step("Sending POST for first new user creation", () ->
                given(requestSpec)
                        .multiPart("username", newUser.getUsername())
                        .multiPart("email", newUser.getEmail())
                        .multiPart("password", newUser.getPassword())
                .when()
                        .post(CREATE_ENDPOINT)
                .then()
                        .spec(responseSpec)
                        .statusCode(200)
        );


        CreateUserNegativeResponseModel response = step("Sending POST for user creation with non unique username", () ->
                given(requestSpec)
                        .multiPart("username", nonUniqueUser.getUsername())
                        .multiPart("email", nonUniqueUser.getEmail())
                        .multiPart("password", nonUniqueUser.getPassword())
                .when()
                        .post(CREATE_ENDPOINT)
                .then()
                        .spec(responseSpec)
                        .statusCode(400)
                        .extract().as(CreateUserNegativeResponseModel.class)
        );

        step("Verifying error message", () -> {
                    assertions.assertThat(response.isSuccess()).isFalse();
                    assertions.assertThat(response.getMessage()[0]).isEqualTo(NON_UNIQUE_USERNAME_MESSAGE);
                    assertions.assertAll();
                }
        );
    }

    @Test
    @DisplayName("User can't be created with non-unique email")
    void verifyNonUniqueEmailNotCreated() {

        CreateUserRequestModel newUser =
                new CreateUserRequestModel(faker.name().username(),
                        faker.internet().emailAddress(),
                        faker.internet().password());

        CreateUserRequestModel nonUniqueUser =
                new CreateUserRequestModel(faker.name().username(),
                        newUser.getEmail(),
                        faker.internet().password());

        step("Sending POST for first new user creation", () ->
                given(requestSpec)
                        .multiPart("username", newUser.getUsername())
                        .multiPart("email", newUser.getEmail())
                        .multiPart("password", newUser.getPassword())
                .when()
                        .post(CREATE_ENDPOINT)
                .then()
                        .spec(responseSpec)
                        .statusCode(200)
        );


        CreateUserNegativeResponseModel response =
                step("Sending POST for user creation with non-unique email", () ->
                given(requestSpec)
                        .multiPart("username", nonUniqueUser.getUsername())
                        .multiPart("email", nonUniqueUser.getEmail())
                        .multiPart("password", nonUniqueUser.getPassword())
                .when()
                        .post(CREATE_ENDPOINT)
                .then()
                        .spec(responseSpec)
                        .statusCode(400)
                        .extract().as(CreateUserNegativeResponseModel.class)
        );


        step("Verifying error message", () -> {
                    assertions.assertThat(response.isSuccess()).isFalse();
                    assertions.assertThat(response.getMessage()[0]).isEqualTo(NON_UNIQUE_EMAIL_MESSAGE);
                    assertions.assertAll();
                }
        );
    }

    @Test
    @DisplayName("Unsuccessful user creation with invalid email")
    void verifyNotCorrectEmailNotCreated() {
        CreateUserRequestModel newUser =
                new CreateUserRequestModel(faker.name().username(),
                        faker.name().username(),
                        faker.internet().password());

        CreateUserNegativeResponseModel response = step("Sending POST user creation with invalid email", () ->
                given(requestSpec)
                        .multiPart("username", newUser.getUsername())
                        .multiPart("email", newUser.getEmail())
                        .multiPart("password", newUser.getPassword())
                .when()
                        .post(CREATE_ENDPOINT)
                .then()
                        .spec(responseSpec)
                        .statusCode(400)
                        .extract().as(CreateUserNegativeResponseModel.class)
        );

        step("Verifying error message", () -> {
                    assertions.assertThat(response.isSuccess()).isFalse();
                    assertions.assertThat(response.getMessage()[0]).isEqualTo("Email is not valid");
                    assertions.assertAll();
                }
        );
    }
}




