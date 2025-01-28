package qa.avasilev.models;

import lombok.Data;

@Data
public class CreateUserPositiveResponseModel {
    boolean success;
    GetUserResponseModel details;
    String message;
}
