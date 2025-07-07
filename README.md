# Technical Task

## Objectives

1. Explore the provided API and document all possible test cases and/or checklists.  
2. Prioritize the test cases.  
3. Automate the most critical test cases, publish both the automation code and the test documentation in this repository.

## Test Cases and Checklists

### Priority 1
- **Successful user creation with valid data.**
- **Successfully retrieving a list of users.**
- **Attempt to create a user with an existing username.**
- **Attempt to create a user with an existing email.**

### Priority 2
- **Creating a user with a missing required field.**
- **Creating a user with an empty required field.**
- **Creating a user with an invalid email format.**
- **Retrieving user via `/get?id`** – undocumented in API spec, otherwise would be higher priority.

### Priority 3
- **Retrieving user via `/get?username`** – undocumented.
- **Retrieving user via `/get?email`** – undocumented.
- **Creating a user with an excessively long `username` (256 characters).**
- **Creating a user with an excessively long `password` (256 characters).**

### Priority 4
- **Creating a user with undocumented parameters in form-data.**
- **Retrieving user via `/get?password`.**
- **Retrieving user using an unknown parameter in `/get?[param]`.**
- **Retrieving user with an existing parameter but invalid value.**
- **Submitting JSON instead of form-data (`Content-Type: application/json`) – note: this currently works.**

### Additional Notes
- Since the `/get?[param=value]` query directly affects SQL queries, SQL injection testing is required.

## Automated Test Cases

### Notes
- Tests are executed in parallel threads.
- Tests are fully independent.
- Assumes test data can only be created during test execution.

### 1. Successful User Creation with Valid Data

**Preconditions:**  
- API is available

**Step 1**  
- Send a POST request with valid `username`, `email`, and `password` as form parameters.

**Expected Result 1**  
- Response with status code `200`, `success: true`, and message `"User Successfully created"`  
- Response includes a non-null `id`, matching user data, and non-empty `created_at` and `updated_at`.

**Step 2**  
- Send a GET request using `/get?id=<user_id>`  
- Compare the returned data to the POST request input.

**Expected Result 2**  
- Response with status code `200`  
- All fields match the previously submitted values.

> Typo discovered during test: `"User **Successully** created"`

### 2. Successfully Retrieving the List of Users

**Preconditions:**  
- API is available

**Step 1**  
- Send 5 POST requests to create users with valid data.

**Expected Result 1**  
- Each request returns a `200` status code.

**Step 2**  
- Send a GET request with no parameters.

**Expected Result 2**  
- Response with status code `200`  
- The array contains at least 5 users, including the ones just created.

### 3. Creating a User with an Existing Username

**Preconditions:**  
- API is available

**Step 1**  
- Send a POST request with valid data.

**Expected Result 1**  
- Response with status code `200`

**Step 2**  
- Send another POST request using the same `username` but a different `email`.

**Expected Result 2**  
- Response with status code `400`  
- Response contains `success: false` and `"message": "This username is taken. Try another."`

### 4. Creating a User with an Existing Email

**Preconditions:**  
- API is available

**Step 1**  
- Send a POST request with valid data.

**Expected Result 1**  
- Response with status code `200`

**Step 2**  
- Send another POST request using the same `email` but a different `username`.

**Expected Result 2**  
- Response with status code `400`  
- Response contains `success: false` and `"message": "Email already exists"`

### 5. Creating a User with Invalid Email Format

> Lower priority but includes a failing case.

**Preconditions:**  
- API is available

**Step**  
- Send a POST request with a valid `username` and `password` but an invalid `email` (e.g., missing `@` or domain).

**Expected Result**  
- Response with status code `400`  
- Contains `success: false` and a relevant error message.

## Project Structure

```
MF-technical-taks/
│
├── src/
│   ├── main/                     # (empty)
│   └── test/
│       └── java/qa/avasilev/
│           ├── helpers/
│           │   └── CustomAllureListener.java
│           ├── models/          # DTOs and response models
│           │   ├── CreateUserNegativeResponseModel.java
│           │   ├── CreateUserPositiveResponseModel.java
│           │   ├── CreateUserRequestModel.java
│           │   └── GetUserResponseModel.java
│           ├── specs/           # RestAssured specifications
│           │   └── UserApiSpec.java
│           └── tests/           # Test classes
│               ├── TestBase.java
│               ├── TestData.java
│               └── UserApiTests.java
│       └── resources/
│           ├── tpl/             # Request/response templates
│           │   ├── request.ftl
│           │   └── response.ftl
│           └── junit-platform.properties
├── .gitignore
└── build.gradle
```

## Technologies Used

- **Java** – Primary language
- **JUnit 5** – Testing framework
- **RestAssured** – REST API testing library
- **Allure** – Test report generation
- **Faker** – Test data generation
- **AssertJ** – Advanced assertion library

## How to Run

### Run Tests

Make sure you have **Java** and **Gradle** installed.

```bash
git clone <repository-url>
cd MF-technical-taks
gradle clean test
```

### Generate Allure Report

```bash
gradle allureReport
```

### View the Report

```bash
gradle allureServe
```

The report will open in your browser.
